package com.galvanize.badgearamareceptiondesk.utility;

import com.galvanize.badgearamareceptiondesk.entity.ExtendedPerson;
import com.galvanize.badgearamareceptiondesk.entity.ExtendedPersonFrontEnd;
import com.galvanize.badgearamareceptiondesk.entity.Person;
import com.galvanize.badgearamareceptiondesk.entity.Visit;
import org.springframework.stereotype.Service;

@Service
public class EntityConverter {


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

    public ExtendedPersonFrontEnd transformPersonToFrontEndPerson(Person person) {
        return ExtendedPersonFrontEnd.builder()
                .phoneNumber(person.getPhoneNumber().toString())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .company(person.getCompany()).build();

    }

    public ExtendedPersonFrontEnd transformVisitToExtendedPersonFrontEnd(Visit visit) {
        String phone = visit.getPhoneNumber().toString().replaceAll("[^0-9]", "");
        return ExtendedPersonFrontEnd.builder()
                .phoneNumber(phone)
                .registerDate(visit.getRegisterDate())
                .status(visit.getStatus())
                .hostName(visit.getHostName())
                .hostPhone(visit.getHostPhoneNumber().toString())
                .purposeOfVisit(visit.getPurposeOfVisit())
                .build();

    }
}
