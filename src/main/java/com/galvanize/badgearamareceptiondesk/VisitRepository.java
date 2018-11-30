package com.galvanize.badgearamareceptiondesk;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VisitRepository extends CrudRepository<Visit, Long> {
    List<Visit> findAllByPhoneNumberAndStatusEqualsAndRegisterDateGreaterThan(Long phoneNumber, VisitStatus status, Date date);
    List<Visit> findAllByPhoneNumberOrderByRegisterDateDesc(Long phoneNumber);

   // List<Visit> findAllByPhoneNumberAndStatusEquals(Long phoneNumber, String status);
//    List<Visit> findAllByPhoneNumberAndStatusEqualsAndRegisterDateGreaterThan(Long phoneNumber, VisitStatus status, Date date);
//    List<Visit> findAllByPhoneNumberAndStatusEqualsAndRegisterDateGreaterThan(Long phoneNumber, VisitStatus status, Date date);
}
