package com.example.studyolle.account;

import com.example.studyolle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("이메일 - 입력값오류")
    void checkEmailToken_with_wrong() throws Exception {

        mockMvc.perform(get("/check-email-token")
                .param("token", "asdfasdf")
                .param("email", "email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("이메일 - 정상값")
    void checkEmailToken() throws Exception {

        Account account = Account.builder()
                .email("test@email.com")
                .password("12345678")
                .nickname("테스트")
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("테스트"));
    }

    @Test
    @DisplayName("회원가입 보이는지 테스트")
    void signUpForm() throws Exception{
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }


    @Autowired private AccountRepository accountRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @DisplayName("회원가입처리 - 입력된 오류값")
    @Test
    void signUpSubmit_fail() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "seonhwa..")
                .param("email", "email..")
                .param("password", "12345")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원가입처리 - 입력된 정상값")
    @Test
    void signUpSubmit_success() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "seonhwa")
                .param("email", "seonflower91@gmail.com")
                .param("password", "123456789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("seonflower91@gmail.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "123456789");
        assertNotNull(account.getEmailCheckToken());
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}
