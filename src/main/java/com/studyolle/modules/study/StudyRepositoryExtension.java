package com.studyolle.modules.study;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {

    Page<Study> findByKeyword(String keyword, Pageable pageable);

    List<Study> findByAccount(Set<Tag> tag, Set<Zone> zones);

    List<Study> findFirst5ByManagerOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Study> findFirst5ByMemberOrderByPublishedDateTimeDesc(Account account, boolean closed);
}
