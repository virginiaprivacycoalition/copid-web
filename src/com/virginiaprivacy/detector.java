/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.1
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.virginiaprivacy;

public class detector {
  public static String get_serialized_frontal_faces() {
    return detectorJNI.get_serialized_frontal_faces();
  }

  public static SWIGTYPE_p_object_detectorT_scan_fhog_pyramidT_pyramid_downT_6_t_t_t get_frontal_face_detector() {
    return new SWIGTYPE_p_object_detectorT_scan_fhog_pyramidT_pyramid_downT_6_t_t_t(detectorJNI.get_frontal_face_detector(), true);
  }

}
