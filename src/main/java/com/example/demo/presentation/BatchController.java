package com.example.demo.presentation;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.config.BatchConfig;

@Controller
@Slf4j
public class BatchController {

    @Autowired
    private BatchConfig batchConfig;

    /**
     * バッチ起動画面を表示
     */
    @GetMapping({ "/admin/batch/start", "/user/batch/start" })
    public String launchBatchForm(Model model) {
        return "batch/start";
    }

    /**
     * バッチ履歴画面を表示
     */
    @GetMapping({ "/admin/batch/history", "/user/batch/history" })
    public String showBatchHistoryForm(Model model) {
        return "batch/history";
    }
}