package vi.wbca.webcinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.RankCustomer;
import vi.wbca.webcinema.service.rankCustomerService.RankCustomerService;
import vi.wbca.webcinema.util.Informations;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rank")
public class RankCustomerController {
    private final RankCustomerService rankCustomerService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertRank(@RequestBody RankCustomer rankCustomer) {
        rankCustomerService.insertRank(rankCustomer);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Informations.POINT, rankCustomer.getPoint().toString());
        responseData.put(Informations.NAME, rankCustomer.getName());
        responseData.put(Informations.DESCRIPTION, rankCustomer.getDescription());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Rank insert successfully.", responseData)
        );
    }

    @GetMapping("/get-all-rank")
    public List<RankCustomer> getAllRank() {
        return rankCustomerService.getAllRank();
    }
}
