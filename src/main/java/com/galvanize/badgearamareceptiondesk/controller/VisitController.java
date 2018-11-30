package com.galvanize.badgearamareceptiondesk.controller;

import com.galvanize.badgearamareceptiondesk.entity.ExtendedPersonFrontEnd;
import com.galvanize.badgearamareceptiondesk.enums.VisitStatus;
import com.galvanize.badgearamareceptiondesk.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/visit")

public class VisitController {

    @Autowired
    private  VisitService visitService;

    private static final Logger LOGGER = LoggerFactory.getLogger(VisitController.class);

    //  HOST change status from WAITING to IN
    @PutMapping("/checkin")
    public String setVisitorCheckIn(@RequestBody ExtendedPersonFrontEnd extendedPersonFrontEnd){
        return visitService.setVisitorCheckIn(extendedPersonFrontEnd);
    }

    // GUARD find all Waiting visitors
    @GetMapping("/visitor/waiting")
    public List<ExtendedPersonFrontEnd> getAllWaitingVisitors() {
        List<ExtendedPersonFrontEnd> extendedPersonFrontEnd = visitService.findWaitingVisitors(VisitStatus.WAITING);
        return extendedPersonFrontEnd;
    }

    // Guard Verify Visitor and change status "UNVERFIED" to "WAITING"
    @PutMapping("/visitor/verify")
    public ExtendedPersonFrontEnd verifyVisitor(@RequestBody ExtendedPersonFrontEnd visitor){
        return visitService.findByPhoneNumberAndCheckedInDateNullAndStatusIs(new Long(visitor.getPhoneNumber()), VisitStatus.UNVERIFIED);

    }

    // Host find all visitors have Status "IN"
    @GetMapping("/visitor/checkedin/")
    public List<ExtendedPersonFrontEnd> getAllInVisitors() {
        List<ExtendedPersonFrontEnd> extendedPersonFrontEnd = visitService.findWaitingVisitors(VisitStatus.IN);
        return extendedPersonFrontEnd;
    }
}