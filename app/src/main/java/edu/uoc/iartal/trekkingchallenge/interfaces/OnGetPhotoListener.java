/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.uoc.iartal.trekkingchallenge.interfaces;

import android.net.Uri;

/**
 * Interface to notify when photo route information is fetched. It allows works with data retrieved in a Firebase listener
 * from another class
 */
public interface OnGetPhotoListener {
    void onSuccess(Uri uri);
    void onFailed(Exception e);
}
