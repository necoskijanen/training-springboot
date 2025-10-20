package com.example.demo.batch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.demo.config.BatchConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * 本番環境用コマンドビルダー
 * コマンド名をそのまま使用（拡張子は付与しない）
 */
@Component
@Profile("prod")
@Slf4j
public class ProdCommandBuilder implements CommandBuilder {

    @Override
    public List<String> buildCommand(BatchConfig.Job job) {
        List<String> command = new ArrayList<>();
        command.add(job.getCommand());
        command.addAll(job.getArguments());

        log.debug("Built prod command: {} with arguments: {}", job.getCommand(), job.getArguments());

        return command;
    }
}
