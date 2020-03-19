package com.note.quartz;

import com.note.service.UsersService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Job类
 */
public class QuartzDemo implements Job {

    @Autowired
    private UsersService usersService;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("Execute...." + new Date());
    }
}
