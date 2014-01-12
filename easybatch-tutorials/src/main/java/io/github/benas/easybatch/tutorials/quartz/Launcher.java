/*
 * The MIT License
 *
 *   Copyright (c) 2014, benas (md.benhassine@gmail.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package io.github.benas.easybatch.tutorials.quartz;

import io.github.benas.easybatch.core.impl.EasyBatchEngine;
import io.github.benas.easybatch.core.impl.EasyBatchEngineBuilder;
import io.github.benas.easybatch.flatfile.FlatFileRecordReader;
import io.github.benas.easybatch.flatfile.filter.StartsWithFlatFileRecordFilter;
import io.github.benas.easybatch.flatfile.dsv.DsvRecordMapper;
import io.github.benas.easybatch.tools.scheduling.EasyBatchScheduler;
import io.github.benas.easybatch.tools.scheduling.EasyBatchSchedulerException;
import io.github.benas.easybatch.tutorials.helloworld.Greeting;
import io.github.benas.easybatch.tutorials.helloworld.GreetingProcessor;
import io.github.benas.easybatch.validation.BeanValidationRecordValidator;

import java.util.Date;

/**
 * Main class to run the Hello World tutorial repeatedly every hour using easy batch - quartz integration module.<br/>
 *
 * The {@link EasyBatchScheduler} API lets you schedule easy batch executions as follows :
 * <ul>
 * <li>At a fixed point of time using {@link EasyBatchScheduler#scheduleAt(java.util.Date)}</li>
 * <li>Repeatedly with predefined interval using {@link EasyBatchScheduler#scheduleAtWithInterval(java.util.Date, int)}</li>
 * <li>Using unix cron-like expression with {@link EasyBatchScheduler#scheduleCron(String)}</li>
 * </ul>
 *
 * @author benas (md.benhassine@gmail.com)
 */
public class Launcher {

    public static void main(String[] args) throws Exception {

        // Build an easy batch engine
        EasyBatchEngine easyBatchEngine = new EasyBatchEngineBuilder()
                .registerRecordReader(new FlatFileRecordReader(args[0]))
                .registerRecordFilter(new StartsWithFlatFileRecordFilter("#"))
                .registerRecordMapper(new DsvRecordMapper<Greeting>(Greeting.class, new String[]{"sequence", "name"}))
                .registerRecordValidator(new BeanValidationRecordValidator<Greeting>())
                .registerRecordProcessor(new GreetingProcessor())
                .build();

        // schedule the engine to start now and run every hour
        try {
            EasyBatchScheduler easyBatchScheduler = new EasyBatchScheduler(easyBatchEngine);
            easyBatchScheduler.scheduleAtWithInterval(new Date(), 60);
            easyBatchScheduler.start();
        } catch (EasyBatchSchedulerException e) {
            System.err.println(e.getMessage());
        }

    }

}