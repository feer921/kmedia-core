package com.jcodeing.kmedia.definition;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/3/12<br>
 * Time: 14:22<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */
public class PlayException extends Exception {
    private String thePlayMediaFilePath;

    private int errCode;

    private int errWhat;

    private String extraInfo;
    public PlayException() {
    }

    public PlayException(Throwable throwable) {
        super(throwable);
    }
    public String getThePlayMediaFilePath() {
        return thePlayMediaFilePath;
    }

    public void setThePlayMediaFilePath(String thePlayMediaFilePath) {
        this.thePlayMediaFilePath = thePlayMediaFilePath;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public int getErrWhat() {
        return errWhat;
    }

    public void setErrWhat(int errWhat) {
        this.errWhat = errWhat;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public String toString() {
        return "PlayException{" +
                "thePlayMediaFilePath='" + thePlayMediaFilePath + '\'' +
                ", errCode=" + errCode +
                ", errWhat=" + errWhat +
                ", extraInfo='" + extraInfo + '\'' +
                '}';
    }
}
