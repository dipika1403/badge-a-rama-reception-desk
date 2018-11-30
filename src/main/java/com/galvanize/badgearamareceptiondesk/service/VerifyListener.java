package com.galvanize.badgearamareceptiondesk.service;

import com.galvanize.badgearamareceptiondesk.ExtendedPerson;
import com.galvanize.badgearamareceptiondesk.VisitStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class VerifyListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyListener.class);

    @Autowired
    VisitService visitService;

    @RabbitListener(queues = "${amqp.queue.name}")
    public void receiveMessageForApp(final ExtendedPerson extendedPerson) throws ParseException {
        LOGGER.info("Received message: {} from sender queue.", extendedPerson);
        if (extendedPerson.getStatus() == null)
            visitService.verifySave(extendedPerson);
        else if(extendedPerson.getStatus().equals(VisitStatus.WAITING) || extendedPerson.getStatus().equals(VisitStatus.IN) || extendedPerson.getStatus().equals(VisitStatus.OUT))
            visitService.verifyUpdate(extendedPerson, extendedPerson.getStatus() );



    }
}
