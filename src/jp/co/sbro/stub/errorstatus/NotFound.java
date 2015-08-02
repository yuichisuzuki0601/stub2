package jp.co.sbro.stub.errorstatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author suzuki_yuu
 *
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFound extends RuntimeException {

}
