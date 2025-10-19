package com.example.demo.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Batch Controller
 * バッチ処理画面のルーティングを担当するコントローラー
 */
@Controller
public class BatchController {

    /// **
    // * Admin - バッチ起動画面
    // */
    // @GetMapping("/admin/batch/start")
    // public String adminBatchStart() {
    // return "batch/start";
    // }

    /// **
    // * Admin - バッチ履歴画面
    // */
    // @GetMapping("/admin/batch/history")
    // public ModelAndView adminBatchHistory() {
    // return new ModelAndView("batch/history");
    // }

    /// **
    // * User - バッチ起動画面
    // */
    // @GetMapping("/user/batch/start")
    // public ModelAndView userBatchStart() {
    // return new ModelAndView("batch/start");
    // }

    /// **
    // * User - バッチ履歴画面
    // */
    // @GetMapping("/user/batch/history")
    // public ModelAndView userBatchHistory() {
    // return new ModelAndView("batch/history");
    // }
}
