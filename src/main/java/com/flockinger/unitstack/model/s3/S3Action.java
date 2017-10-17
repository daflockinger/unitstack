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
package com.flockinger.unitstack.model.s3;

/**
 * Classifications for API actions on the S3 mock
 * to their corresponding Responder classes.
 *
 */
public enum S3Action {
  COPY_OBJECT, 
  CREATE_BUCKET, 
  DELETE_BUCKET, 
  DELETE_OBJECT, 
  DELETE_OBJECTS, 
  OBJECT_EXISTS, 
  GET_BUCKET_ACL, 
  GET_BUCKET_LOCATION, 
  GET_OBJECT_ACL, 
  GET_OBJECT, 
  LIST_BUCKETS, 
  LIST_OBJECTS, 
  MULTIPART_UPLOAD, 
  PUT_OBJECT, 
  SET_BUCKET_ACL, 
  SET_OBJECT_ACL;
}
