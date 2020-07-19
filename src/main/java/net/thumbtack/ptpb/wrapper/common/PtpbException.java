package net.thumbtack.ptpb.wrapper.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PtpbException extends Exception {

    @Singular
    private final List<ErrorCode> errors;

    public PtpbException(ErrorCode error) {
        super(error.toString());
        errors = Collections.singletonList(error);
    }
}
