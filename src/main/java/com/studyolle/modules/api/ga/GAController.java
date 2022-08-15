package com.studyolle.modules.api.ga;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 *
 * Project : studyolle
 * Package : com.studyolle.modules.api.ga
 * ClassName : GAController
 * Created : 2022-08-05 SEONHWA
 * </pre>
 *
 * @author SEONHWA
 * @since 2022-08-05 오후 2:37
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ga")
public class GAController {

    private final GAService gaService;

    @GetMapping("/sample")
    public Object sampleData() {
        return gaService.getData();
    }


}
