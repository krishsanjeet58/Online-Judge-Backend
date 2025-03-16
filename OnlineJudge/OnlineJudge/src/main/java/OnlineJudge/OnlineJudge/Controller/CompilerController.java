package OnlineJudge.OnlineJudge.Controller;

import OnlineJudge.OnlineJudge.Service.CompilerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController    // This means that this class is a Controller
@RequestMapping(path="/compiler") // This means URL's start with /compiler after Application path
public class CompilerController {

    private CompilerService compilerService;
    public CompilerController(CompilerService compilerService) {
        this.compilerService = compilerService;
    }
    @PostMapping("/run")
    public ResponseEntity<String> compileCode(@RequestHeader(value = "language",defaultValue = "java") String language, @RequestBody(required = false) String code  ) {
        if(code==null || code.isEmpty())
        {
            return  new ResponseEntity<>("Error : code is required ",HttpStatus.BAD_REQUEST);
        }
        String result =compilerService.saveCodeToFileAndExecudeCode(code,language);

        if(result==null)
        {
            return  new ResponseEntity<>("Error : code is required ",HttpStatus.BAD_REQUEST);
        }
        return  new ResponseEntity<>(result ,HttpStatus.OK);
    }



}
