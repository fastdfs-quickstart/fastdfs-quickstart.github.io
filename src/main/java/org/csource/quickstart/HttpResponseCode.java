package org.csource.quickstart;

public interface HttpResponseCode {

  int SUCCESS_CODE = 200;
  
  int PARAM_DISPLAY = 201;

  int PARAM_VALUE_ERROR = 204;

  int CLIENT_PARAM_INVALID = 206;

  int FILE_NOT_EXISTS = 207;

  int PROTOCOL_STACK_OUT_OF_RANGE = 210;

  int REQUEST_TOKEN_INVALID = 400;

  int SERVER_ERROR = 500;

  int SERVER_IO_ERROR = 501;

  int SERVER_Env_ERROR = 502;

  int SERVER_IO_READ = 503;

  int SERVER_IO_WRITE = 504;

  int SERVER_DB_ERROR = 505;

  int UPLOAD_FILE_FORMAT_ERROR = 506;

  int SERVER_TO_FastDFS_ERROR = 507;
  
  //
  int ERRCODE_LOGINUSER_INVALID = 10014;

  
}
