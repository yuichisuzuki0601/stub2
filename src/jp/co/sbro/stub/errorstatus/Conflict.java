package jp.co.sbro.stub.errorstatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author suzuki_yuu
 *
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class Conflict extends RuntimeException {

}
