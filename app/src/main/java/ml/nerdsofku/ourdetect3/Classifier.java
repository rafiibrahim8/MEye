/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package ml.nerdsofku.ourdetect3;

import android.graphics.Bitmap;
import java.util.List;


/**
 * Generic interface for interacting with different recognition engines.
 */
public interface Classifier {
    /**
     * An immutable result returned by a Classifier describing what was recognized.
     */
    class Recognition {
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of
         * the object.
         */
        private final String id;

        /**
         * Display name for the recognition.
         */
        private final String title;

        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         */
        private final Float confidence;
        private final byte aByte;

        public Recognition(
                final String id, final String title, final Float confidence,byte bConf) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.aByte = bConf;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title.toUpperCase().charAt(0)+title.substring(1);
        }

        public Float getConfidence() {
            return confidence;
        }

        public String getConfidenceString() {
            float conf = (aByte & 0xFF)/((float)255/100);
            return String.format("(%.1f%%) ", conf);
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }

            if (title != null) {
                resultString += title + " ";
            }

            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }

            return resultString.trim();
        }
    }


    List<Recognition> recognizeImage(Bitmap bitmap);

    void close();
}