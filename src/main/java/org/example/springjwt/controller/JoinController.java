package org.example.springjwt.controller;

import org.example.springjwt.dto.JoinDTO;
import org.example.springjwt.service.JoinService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinController {
    // JoinService를 사용할 것이기 때문에 주입받을 객체변수를 만들고
    private final JoinService joinService;
    // JoinController의 생성자 방식으로 JoinService를 주입받으면 된다.
    public JoinController(JoinService joinService){
        this.joinService = joinService;
    }
    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO){
        joinService.joinProcess(joinDTO);

        return "OK";
    }
}
