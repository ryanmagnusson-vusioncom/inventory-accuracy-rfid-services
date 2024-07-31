package io.vusion.vtransmit.v2.commons.exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ErrorCodes extends ArrayList<ErrorCode> {

    @Override
    public boolean add(ErrorCode errorCode) {
        return errorCode != null && super.add(errorCode);
    }

    public ErrorCode addError(String code) {
        final ErrorCode errorCode = new ErrorCode(code, new ArrayList<>());
        add(errorCode);
        return errorCode;
    }

    public ErrorCode addError(String code, String parm) {
        final List<String> parmList = new ArrayList<>();
        parmList.add(parm);
        final ErrorCode errorCode = new ErrorCode(code, parmList);
        add(errorCode);
        return errorCode;
    }

    public ErrorCode addError(String code, String p1, String p2) {
        final ErrorCode errorCode = new ErrorCode(code, Arrays.asList(p1, p2));
        add(errorCode);
        return errorCode;
    }

    public ErrorCode addError(String code, String p1, String p2, String p3) {
        final ErrorCode errorCode = new ErrorCode(code, List.of(p1, p2, p3));
        add(errorCode);
        return errorCode;
    }

    public ErrorCode addError(String code, String p1, String p2, String p3, String... parms) {
        final List<String> parmList = new ArrayList<>();
        parmList.add(p1);
        parmList.add(p2);
        parmList.add(p3);
        if (parms != null && parms.length > 0) {
            parmList.addAll(Arrays.asList(parms));
        }
        final ErrorCode errorCode = new ErrorCode(code, parmList);
        add(errorCode);
        return errorCode;
    }

    public ErrorCode addError(String code, List<String> parms) {
        final List<String> parmList = Optional.ofNullable(parms).orElseGet(ArrayList::new);

        final ErrorCode errorCode = new ErrorCode(code, parmList);
        add(errorCode);
        return errorCode;
    }




}
