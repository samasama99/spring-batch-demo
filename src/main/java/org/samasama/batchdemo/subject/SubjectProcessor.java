package org.samasama.batchdemo.subject;

import org.springframework.batch.item.ItemProcessor;

public class SubjectProcessor implements ItemProcessor<Subject, Subject> {

    @Override
    public Subject process(Subject item) throws Exception {
        item.setId(null);
        return item;
    }
}
