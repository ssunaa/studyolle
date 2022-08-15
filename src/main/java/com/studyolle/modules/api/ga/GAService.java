package com.studyolle.modules.api.ga;

import com.studyolle.modules.study.Study;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class GAService {

    public Object getData() {

        Map<String, String> map = new HashMap<>();
        map.put("과일", "사과");

        return map;
    }


}
