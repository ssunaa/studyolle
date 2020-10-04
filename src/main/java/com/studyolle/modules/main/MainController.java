package com.studyolle.modules.main;

import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.CurrentAccount;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.EnrollmentRepository;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);

            Account myInfo = accountRepository.findTagsZonesById(account.getId());

            //TODO 관심주제, 지역정보 목록 조회
            model.addAttribute(myInfo);
            model.addAttribute("myTagList", myInfo.getTags());
            model.addAttribute("myZoneList", myInfo.getZones());

            //TODO 관리중인 스터디 목록 조회
            model.addAttribute("studyManagerOf", studyRepository.findFirst5ByManagerOrderByPublishedDateTimeDesc(account, false));

            //TODO 참여중인 스터디 목록 조회
            model.addAttribute("studyMemberOf", studyRepository.findFirst5ByMemberOrderByPublishedDateTimeDesc(account, false));

            //TODO 참석할 스터디의 모임 목록 조회
            model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(myInfo, true));

            //TODO 관심있는 스터디 목록 조회(주제, 지역)
            model.addAttribute("studyList", studyRepository.findByAccount(myInfo.getTags(), myInfo.getZones()));

            return "index-after-login";
        }

        model.addAttribute("studyList", studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/search/study")
    public String serachStudy(String keyword, Model model,
                              @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC)
                                      Pageable pageable) {
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("studyPage", studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "search";
    }

}
