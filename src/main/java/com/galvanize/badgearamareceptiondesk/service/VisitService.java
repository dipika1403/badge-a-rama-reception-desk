package com.galvanize.badgearamareceptiondesk.service;

import com.galvanize.badgearamareceptiondesk.entity.ExtendedPerson;
import com.galvanize.badgearamareceptiondesk.entity.ExtendedPersonFrontEnd;
import com.galvanize.badgearamareceptiondesk.entity.Visit;
import com.galvanize.badgearamareceptiondesk.enums.VisitStatus;
import com.galvanize.badgearamareceptiondesk.exception.*;
import com.galvanize.badgearamareceptiondesk.repository.VisitRepository;
import com.galvanize.badgearamareceptiondesk.utility.EntityConverter;
import com.galvanize.badgearamareceptiondesk.utility.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VisitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisitService.class);
    @Autowired
    VisitRepository visitRepository;
    @Autowired
    EntityConverter entityConverter;
    private Date dateInTime = null;
    private Date dateOutTime = null;
    private boolean activeStatus = false;

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
            updatePerson = (Visit) visits.get(0);
            visitId = visits.get(0).getId();

            LOGGER.info("visitId :" + visitId);
            LOGGER.info("visits :" + visits.toString());
            LOGGER.info("visits :" + visits.size());
        }

        if (extendedPerson.getStatus().equals(VisitStatus.IN) && !updatePerson.getStatus().equals(VisitStatus.IN)) {
            dateInTime = new Date();
        }
        if (extendedPerson.getStatus().equals(VisitStatus.OUT) && !updatePerson.getStatus().equals(VisitStatus.OUT)) {
            dateOutTime = new Date();
        }

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

    @Transactional
    public String setVisitorCheckIn(ExtendedPersonFrontEnd extendedPersonFrontEnd) {
        String response = UpdateExtendedPersonFrontEnd(extendedPersonFrontEnd , VisitStatus.IN);
        return response;

    }

    public String UpdateExtendedPersonFrontEnd(ExtendedPersonFrontEnd extendedPersonFrontEnd, VisitStatus status) {
        String phoneNumber = extendedPersonFrontEnd.getPhoneNumber();
        Long phone = Long.parseLong(phoneNumber.replaceAll("[^0-9]", ""));
        try {

            System.out.println("***** phone :" + phone);

            List<Visit> visits = visitRepository.findAllByPhoneNumberAndCheckedInDateNullAndStatusIs(phone, VisitStatus.WAITING);
            Long visitId = null;
            Visit updatePerson = new Visit();
            if (visits.size() > 0) {
                updatePerson = (Visit) visits.get(0);
                visitId = visits.get(0).getId();

                LOGGER.info("visitId :" + visitId);
                LOGGER.info("visits :" + visits.toString());
                LOGGER.info("visits :" + visits.size());

            }

            if (status.equals(VisitStatus.IN)) {
                updatePerson.setStatus(VisitStatus.IN);
            }
            if (status.equals(VisitStatus.IN) && !updatePerson.getStatus().equals(VisitStatus.WAITING)) {
                dateInTime = new Date();
                activeStatus = true;
            }
            if (status.equals(VisitStatus.OUT) && !updatePerson.getStatus().equals(VisitStatus.OUT)) {
                dateOutTime = new Date();
                activeStatus = false;
            }

       // ExtendedPerson extendedPerson = transformFrontEndPerson(extendedPersonFrontEnd);

            //sendMessage(appExchangeName, appRoutingKey, extendedPerson);
            // save to DB
            visitRepository.save(Visit.builder()
                    .id(updatePerson.getId())
                    .phoneNumber(updatePerson.getPhoneNumber())
                    .hostName(updatePerson.getHostName())
                    .hostPhoneNumber(updatePerson.getHostPhoneNumber())
                    .purposeOfVisit(updatePerson.getPurposeOfVisit())
                    .checkedInDate(dateInTime)
                    .checkedOutDate(dateOutTime)
                    .active(activeStatus)
                    .registerDate(updatePerson.getRegisterDate())
                    .status(updatePerson.getStatus())
                    .build());

            return String.format("Status changed to %s",status) ;
        }
        catch(StatusUpdateFailureException ex){
            return "";      // As per Ray's front end wants to consume.
        }
    }

    public ExtendedPerson transformFrontEndPerson(ExtendedPersonFrontEnd personFE) {
        return ExtendedPerson.builder()
                .phoneNumber(Long.parseLong(personFE.getPhoneNumber().replaceAll("[^0-9]", "")))
                .firstName(personFE.getFirstName())
                .lastName(personFE.getLastName())
                .company(personFE.getCompany())
                .hostName(personFE.getHostName())
                .hostPhoneNumber(Long.parseLong(personFE.getHostPhone().replaceAll("[^0-9]", "")))
                .purposeOfVisit(personFE.getPurposeOfVisit())
                .reasonForDeletion(personFE.getReasonForDeletion())
                .badgeNumber(personFE.getBadgeNumber())
                .active(personFE.getActive())
                .status(personFE.getStatus())
                .build();
    }


    public List<ExtendedPersonFrontEnd> findWaitingVisitors(VisitStatus status1) {
        List<ExtendedPersonFrontEnd> result = new ArrayList<>();

        List<Visit> waitingList = visitRepository.findAllByStatus(VisitStatus.WAITING);
        //findAllByStatusMatchesAndCheckedInDateIsNullAndRegisterDateIsNotNull(VisitStatus.UNVERIFIED, VisitStatus.WAITING);
        // LOGGER.info(" ************ visits :" + waitingList.toString());
        for (Visit visit : waitingList) {
            result.add(entityConverter.transformVisitToExtendedPersonFrontEnd(visit));
//        }
        }
        return result;

    }

    public ExtendedPersonFrontEnd findByPhoneNumberAndCheckedInDateNullAndStatusIs(Long phoneNumber, VisitStatus status){
       List<Visit> visits =  visitRepository.findAllByPhoneNumberAndCheckedInDateNullAndStatusIs(phoneNumber, status);
        Visit updatePerson = null;
       Long visitId = null ;
        if (visits.size() > 0) {
            updatePerson = (Visit) visits.get(0);
            visitId = visits.get(0).getId();


            visitRepository.save(Visit.builder()
                    .id(updatePerson.getId())
                    .phoneNumber(updatePerson.getPhoneNumber())
                    .hostName(updatePerson.getHostName())
                    .hostPhoneNumber(updatePerson.getHostPhoneNumber())
                    .purposeOfVisit(updatePerson.getPurposeOfVisit())
                    .checkedInDate(dateInTime)
                    .checkedOutDate(dateOutTime)
                    .active(activeStatus)
                    .registerDate(updatePerson.getRegisterDate())
                    .checkedOutDate(new Date())
                    .status(VisitStatus.WAITING)
                    .build());
        }
        return entityConverter.transformVisitToExtendedPersonFrontEnd(updatePerson);

    }

    // find all visitor having status = IN


}
