package com.bmbstack.kit.api;

import okhttp3.Response;

public class NetException extends Exception {

  private Response response = null;

  public NetException(Response response) {
    this.response = response;
  }

  public int getStatusCode() {
    if (this.response != null) {
      return this.response.code();
    }
    return -1;
  }

  public String getMessage() {
    if (this.response != null) {
      return this.response.message();
    }
    return "";
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("NetException{");
    sb.append("response=").append(response);
    sb.append('}');
    return sb.toString();
  }
}
