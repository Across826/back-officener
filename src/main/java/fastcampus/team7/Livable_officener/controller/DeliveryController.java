package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.domain.Bank;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.repository.BankRepository;
import fastcampus.team7.Livable_officener.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room")
public class DeliveryController {

    private final DeliveryService deliveryService;

    private final BankRepository bankRepository;

    @GetMapping("/test")
    public String test() {
        return "ok";
    }

    @GetMapping("/bankList")
    public ResponseEntity<APIDataResponse<Map<String, List<Map<String, String>>>>> bankList() {
        List<Bank> bankList = bankRepository.findAll();

        List<Map<String, String>> responseData = bankList.stream()
                .map(bank -> {
                    Map<String, String> bankMap = new HashMap<>();
                    bankMap.put("bankName", bank.getName().getName());
                    return bankMap;
                })
                .collect(Collectors.toList());

        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("banks", responseData);

        return APIDataResponse.of(HttpStatus.OK, "성공", response);
    }
}