package com.welfarerobotics.welfareapplcation.bot.brain.chat.scenario;

import com.welfarerobotics.welfareapplcation.bot.brain.chat.state.ChatState;

import java.io.IOException;

/**
 * @author : Hyunwoong
 * @when : 5/28/2019 2:28 AM
 * @homepage : https://github.com/gusdnd852
 */
public class PardonScenario {
    public static ChatState process(String speech) throws IOException {
        return ChatState.NORMAL_STATE;
    }
}