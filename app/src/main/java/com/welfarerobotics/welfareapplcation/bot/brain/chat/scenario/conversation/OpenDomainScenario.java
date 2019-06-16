package com.welfarerobotics.welfareapplcation.bot.brain.chat.scenario.conversation;

import android.app.Activity;
import android.widget.TextView;
import com.welfarerobotics.welfareapplcation.R;
import com.welfarerobotics.welfareapplcation.bot.Mouth;
import com.welfarerobotics.welfareapplcation.bot.brain.Brain;
import com.welfarerobotics.welfareapplcation.bot.brain.Hormone;
import com.welfarerobotics.welfareapplcation.bot.brain.Pituitary;
import com.welfarerobotics.welfareapplcation.bot.brain.chat.crawler.ModelApi;
import com.welfarerobotics.welfareapplcation.bot.brain.chat.preprocess.NameReplacer;
import com.welfarerobotics.welfareapplcation.util.Pool;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author : Hyunwoong
 * @when : 5/28/2019 1:06 AM
 * @homepage : https://github.com/gusdnd852
 */
public class OpenDomainScenario {
    public static void process(String intent, String speech, Activity activity) throws IOException, ExecutionException, InterruptedException {
        String answer = ModelApi.getOpenDomainAnswer(speech);
        TextView view = activity.findViewById(R.id.hormone); // 호르몬 뷰어

        if (answer.contains("#")) { // 욕설이 포함된 경우
            answer = answer.replaceAll("#", ""); // 대답에서 # 제거
            speech = speech + "#"; // 사용자 입력에 # 추가
        }
        String finalSpeech = speech;
        Future<Hormone> hormoneFuture = Pool.threadPool.submit(() -> {
            Pituitary.rememberNewSentence(finalSpeech);
            return Pituitary.getHormone();
        });
        Brain.hippocampus.decideToSay(NameReplacer.replaceName(answer));
        String currHormone = hormoneFuture.get().toString();
        activity.runOnUiThread(() -> view.setText(currHormone));
        Mouth.get().say();
    }
}
