package com.example.demo.batch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.demo.config.BatchConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * 開発環境用コマンドビルダー
 * OSに応じて自動的に拡張子（.sh / .bat）を付与する
 */
@Component
@Profile({ "dev", "test" })
@Slf4j
public class DevCommandBuilder implements CommandBuilder {

    @Override
    public List<String> buildCommand(BatchConfig.Job job) {
        List<String> command = new ArrayList<>();
        String commandWithExtension = addOsSpecificExtension(job.getCommand());
        command.add(commandWithExtension);
        command.addAll(job.getArguments());

        log.debug("Built dev command: {} with arguments: {}", commandWithExtension, job.getArguments());

        return command;
    }

    /**
     * OSに応じて拡張子を付与する
     * 
     * @param command コマンド名（拡張子なし）
     * @return 拡張子付きコマンド名
     */
    private String addOsSpecificExtension(String command) {
        String osName = System.getProperty("os.name").toLowerCase();
        String extension = osName.contains("windows") ? ".bat" : ".sh";
        String commandWithExtension = command + extension;
        log.info("OS: {}, Command: {} -> {}", osName, command, commandWithExtension);
        return commandWithExtension;
    }
}
