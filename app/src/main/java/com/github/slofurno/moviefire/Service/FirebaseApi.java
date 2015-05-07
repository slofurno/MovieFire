package com.github.slofurno.moviefire.Service;

/**
 * Created by slofurno on 5/5/2015.
 */

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class FirebaseApi {

    String _url;
    Gson _gson;
    private static final ExecutorService pool = Executors.newFixedThreadPool(20);

    public FirebaseApi(String url){
        this._url = url;
        _gson =new Gson();
    }

    public <G> List<G> Search(String term, final Class<G> type) throws MalformedURLException, InterruptedException, ExecutionException{

        String cleaned = term.toLowerCase().replaceAll("\u0020","");
        System.out.println(term + " : " + cleaned);

        String uri = _url + ".json?orderBy=\"$key\"&limitToFirst=5&startAt=\"" + cleaned + "\"";
        URL url = new URL(uri);
        String json = downloadAsync(url).get();

        List<String>innerobjects = new ArrayList<>();
        int depth = 0;
        int startpos = -1;

        for(int i = 0; i < json.length();i++){
            char c = json.charAt(i);
            if (c=='{'){
                depth++;
            }
            else if (c=='}'){
                if (depth>1){
                    depth--;
                }
                //either the last object or empty response
                else if(startpos>=0 && i-startpos>=3){
                    innerobjects.add(json.substring(startpos,i));
                }
            }
            else if (startpos == -1 && depth == 1){
                startpos = i;
            }
            else if (depth==1 && c==','){
                innerobjects.add(json.substring(startpos,i));
                startpos = -1;
            }
        }

        List<G> results = new ArrayList<>();

        for(String inner : innerobjects){
            //movie titles with : in them...
            int index = inner.indexOf(":{");
            String innerjson = inner.substring(index + 1, inner.length());
            G innerresult = _gson.fromJson(innerjson, type);
            results.add(innerresult);
        }
        return results;
    }

    public <G> List<G> Get(List<String>keys, Class<G> type) throws MalformedURLException, InterruptedException, ExecutionException{
        List<URL> urls = new ArrayList<>();

        for(String key:keys){
            urls.add(new URL(_url + "/" + key +  ".json"));
        }

        List<String> jsonlist = downloadAllAsync(urls);
        List<G> results = new ArrayList<>();

        for(String json:jsonlist){
            results.add(_gson.fromJson(json, type));
        }
        return results;
    }

    private List<String> downloadAllAsync(List<URL>urls) throws MalformedURLException, InterruptedException, ExecutionException{
        List<Future<String>>futures = new ArrayList<>();

        for(URL url : urls){
            futures.add(downloadAsync(url));
        }

        List<String>results = new ArrayList<>();

        for(Future<String>future : futures){
            String result = future.get();
            results.add(result);
        }
        return results;
    }

    private Future<String> downloadAsync(final URL url){
        return pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {

                try (InputStream input = url.openStream()) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    char[] buf = new char[4096];
                    int len = 0;
                    int next = 0;

                    while ((next = reader.read(buf,len,4096-len))!= -1){
                        len+=next;
                    }
                    return new String(buf, 0, len);
                }
            }
        });
    }
}