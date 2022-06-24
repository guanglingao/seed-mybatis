package org.seed.mybatis.maven;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


@Mojo(name = "Single", defaultPhase = LifecyclePhase.NONE)
@Slf4j
public class OneModuleGenerate extends AbstractMojo {

    @Parameter(property = "configPath", name = "configPath", defaultValue = "${basedir}")
    private String configPath;

    @SneakyThrows
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            CodeGenerateContext.setSingleModuleMode();

            // 生成DAO、Service、Controller、Entity代码
            CodesBuilder.buildCodeFiles(getConfigPath());
            // 生成其他通用代码
            CommonsBuilder.buildCommonFiles(getConfigPath());
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            System.err.println(stackTrace);
        }
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String tomlConfigPath) {
        this.configPath = tomlConfigPath;
    }


}
