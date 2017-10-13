/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.flockinger.unitstack.response.s3;



import static com.flockinger.unitstack.model.HttpMethod.DELETE;
import static com.flockinger.unitstack.model.HttpMethod.GET;
import static com.flockinger.unitstack.model.HttpMethod.HEAD;
import static com.flockinger.unitstack.model.HttpMethod.POST;
import static com.flockinger.unitstack.model.HttpMethod.PUT;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.HttpMethod;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.S3Action;
import com.flockinger.unitstack.transformer.S3RequestTransformer;


public class S3ActionInvestigator extends S3Responder {

  private static final S3ActionInvestigator instance = new S3ActionInvestigator();

  public static S3ActionInvestigator get() {
    return instance;
  }

  public boolean isAction(MockRequest request, S3Action action) {
    boolean isAction = false;
    switch (action) {
      case GET_BUCKET_ACL:
        isAction = isMethod(request, GET) && hasAction(request, "acl") && noObjectKey(request);
        break;
      case GET_BUCKET_LOCATION:
        isAction = isMethod(request, GET) && hasAction(request, "location");
        break;
      case GET_OBJECT_ACL:
        isAction = isMethod(request, GET) && hasAction(request, "acl") && hasObjectKey(request);
        break;
      case GET_OBJECT:
        isAction = isMethod(request, GET) && noAction(request) && hasObjectKey(request);
        break;
      case LIST_BUCKETS:
        isAction =
            isMethod(request, GET) && noAction(request) && noXml(request) && noObjectKey(request);
        break;
      case LIST_OBJECTS:
        isAction = isMethod(request, GET) && hasAction(request, "prefix=", "encoding-type=",
            "delimiter=", "list-type=", "max-keys=", "uploads&");
        break;
      case COPY_OBJECT:
        isAction = isMethod(request, PUT) && noXml(request) && hasObjectKey(request);
        break;
      case CREATE_BUCKET:
        isAction = isMethod(request, PUT) && noXml(request) && noObjectKey(request);
        break;
      case PUT_OBJECT:
        isAction = isMethod(request, PUT) && hasXml(request) && hasObjectKey(request)
            && !hasXmlWith(request, "AccessControlPolicy") && !url(request).contains("?tagging");
        break;
      case SET_BUCKET_ACL:
        isAction = isMethod(request, PUT) && hasXmlWith(request, "AccessControlPolicy")
            && noObjectKey(request);
        break;
      case SET_OBJECT_ACL:
        isAction = isMethod(request, PUT) && hasXmlWith(request, "AccessControlPolicy")
            && hasObjectKey(request);
        break;
      case DELETE_BUCKET:
        isAction = isMethod(request, DELETE) && noXml(request) && noObjectKey(request);
        break;
      case DELETE_OBJECT:
        isAction = isMethod(request, DELETE) && noAction(request) && hasObjectKey(request);
        break;
      case DELETE_OBJECTS:
        isAction = isMethod(request, POST) && url(request).contains("?delete");
        break;
      case MULTIPART_UPLOAD:
        isAction = isMethod(request, POST) && hasObjectKey(request)
            && StringUtils.containsAny(url(request), "?uploads", "uploadId=");
        break;
      case OBJECT_EXISTS:
        isAction = isMethod(request, HEAD) && hasObjectKey(request);
        break;
      default:
        break;
    }
    return isAction;
  }

  private String url(MockRequest request) {
    return request.getBodyParameters().get(S3RequestTransformer.PARAMETER_URL_NAME);
  }

  private boolean isMethod(MockRequest request, HttpMethod method) {
    String requestMethod = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    return StringUtils.equals(method.toString(), requestMethod);
  }

  private boolean hasXmlWith(MockRequest request, String xmlContent) {
    String xmlBody = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);
    return StringUtils.contains(xmlBody, xmlContent);
  }

  private boolean hasXml(MockRequest request) {
    String xmlBody = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);
    return StringUtils.isNotEmpty(xmlBody);
  }

  private boolean noXml(MockRequest request) {
    return !hasXml(request);
  }

  private boolean hasAction(MockRequest request, String... action) {
    String requestAction = request.getBodyParameters().get(S3RequestTransformer.ACTION);
    return StringUtils.containsAny(requestAction, action);
  }

  private boolean noAction(MockRequest request) {
    String action = request.getBodyParameters().get(S3RequestTransformer.ACTION);
    return StringUtils.isEmpty(action);
  }

  private boolean hasObjectKey(MockRequest request) {
    return getObjectKey(request).isPresent();
  }

  private boolean noObjectKey(MockRequest request) {
    return !hasObjectKey(request);
  }

  @Override
  public boolean isSameAction(MockRequest request) {
    return false;
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    return null;
  }
}
