package com.welfarerobotics.welfareapplcation.api.chat;

import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import com.welfarerobotics.welfareapplcation.util.ApiKeys;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public final class CssApi {
    private static CssApi api = null;
    private MediaPlayer audioPlayer = new MediaPlayer();

    private CssApi() {

    }

    public synchronized static CssApi get() {
        if (api == null) api = new CssApi();
        return api;
    }

    public CssApi play(String tts, String speaker) {
        try {
            String text = URLEncoder.encode(tts, "UTF-8"); // 13자
            String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", ApiKeys.clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", ApiKeys.clientSecret);
            // post request
            String postParams = "speaker=" + speaker + "&speed=2.5&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];
                //NaverCSS 폴더 생성
                File dir = new File(Environment.getExternalStorageDirectory() + "/", "NaverCSS");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 랜덤한 이름으로 mp3 파일 생성
                String tempname = "navercssfile";
                File f = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "NaverCSS/" + tempname + ".mp3");
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read = is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                is.close();

            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String tempname = "navercssfile";
        String Path_to_file = Environment.getExternalStorageDirectory() +
                File.separator + "NaverCSS/" + tempname + ".mp3";

        if (audioPlayer == null)
            audioPlayer = new MediaPlayer();

        try {
            audioPlayer.setDataSource(Path_to_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            audioPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioPlayer.start();
        return this;
    }


    public void stop(Runnable nextAction) {
        if (audioPlayer != null) {
            audioPlayer.setOnCompletionListener(mediaPlayer -> {
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(nextAction, 300);
                audioPlayer.release();
                audioPlayer = null;
            });
        }
    }

    public void cancel() {
        if (audioPlayer != null) {
            audioPlayer.release();
            audioPlayer = null;
        }
    }
}