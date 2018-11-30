package com.galvanize.badgearamareceptiondesk.repository;

import com.galvanize.badgearamareceptiondesk.entity.ExtendedPerson;
import com.galvanize.badgearamareceptiondesk.enums.VisitStatus;
import com.galvanize.badgearamareceptiondesk.entity.Visit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface VisitRepository extends CrudRepository<Visit, Long> {

    List<Visit> findAllByPhoneNumberAndStatusEqualsAndRegisterDateGreaterThan(Long phoneNumber, VisitStatus status, Date date);
    List<Visit> findAllByPhoneNumberOrderByRegisterDateDesc(Long phoneNumber);
    List<Visit> findAllByStatus(VisitStatus status);
    List<Visit> findAllByPhoneNumberAndCheckedInDateNullAndStatusIs(Long phoneNumber, VisitStatus status);
//  List<Visit> findAllByPhoneNumberAndStatusEqualsAndRegisterDateGreaterThan(Long phoneNumber, VisitStatus status, Date date);
//  List<Visit> findAllByPhoneNumberAndStatusEqualsAndRegisterDateGreaterThan(Long phoneNumber, VisitStatus status, Date date);
}
