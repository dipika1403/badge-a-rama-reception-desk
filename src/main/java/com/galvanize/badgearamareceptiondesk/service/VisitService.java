package com.galvanize.badgearamareceptiondesk.service;

import com.galvanize.badgearamareceptiondesk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class VisitService {

    @Autowired

    VisitRepository visitRepository;
    private Date dateInTime = null;
    private Date dateOutTime = null;

    public void verifySave(ExtendedPerson extendedPerson) {

        List<Visit> visits = visitRepository.findAllByPhoneNumberAndStatusEqualsAndRegisterDateGreaterThan(
                extendedPerson.getPhoneNumber(), VisitStatus.UNVERIFIED, Utils.atStartOfDay(new Date()));
        Long visitId = null;
        if (visits.size() > 0)
            visitId = visits.get(0).getId();


        visitRepository.save(Visit.builder()
                .id(visitId)
                .phoneNumber(extendedPerson.getPhoneNumber())
                .hostName(extendedPerson.getHostName())
                .hostPhoneNumber(extendedPerson.getHostPhoneNumber())
                .purposeOfVisit(extendedPerson.getPurposeOfVisit())
                .active(true)
                .registerDate(new Date())
                .status(VisitStatus.UNVERIFIED)
                .build());

    }

    public void verifyUpdate(ExtendedPerson extendedPerson, VisitStatus newStatus) {

        List<Visit> visits = visitRepository.findAllByPhoneNumberOrderByRegisterDateDesc(
                extendedPerson.getPhoneNumber());

        Long visitId = null;
        Visit updatePerson = null;
        if (visits.size() > 0) {
            updatePerson = (Visit)visits.get(0);

            visitId = visits.get(0).getId();

            System.out.println("************ visitId :" + visitId);
            System.out.println("************ visits :" + visits.toString());
            System.out.println("************ visits :" + visits.size());
        }

        if (extendedPerson.getStatus().equals(VisitStatus.IN) && !updatePerson.getStatus().equals(VisitStatus.IN)) { dateInTime = new Date(); }
        if (extendedPerson.getStatus().equals(VisitStatus.OUT) && !updatePerson.getStatus().equals(VisitStatus.OUT)) { dateOutTime = new Date(); }

        visitRepository.save(Visit.builder()
                .id(updatePerson.getId())
                .phoneNumber(updatePerson.getPhoneNumber())
                .hostName(updatePerson.getHostName())
                .hostPhoneNumber(updatePerson.getHostPhoneNumber())
                .purposeOfVisit(updatePerson.getPurposeOfVisit())
                .checkedInDate(dateInTime)
                .checkedOutDate(dateOutTime)
                .active(true)
                .registerDate(updatePerson.getRegisterDate())
                .checkedOutDate(new Date())
                .status(newStatus)
                .build());

    }
}
