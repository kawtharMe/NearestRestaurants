# NearestRestaurants
In this project you can find two things: 
1/ create an overpass query to get the nearest restaurants for example 
2/ use an open street map    
      In your gradle file add:
  implementation 'org.osmdroid:osmdroid-android:5.6.5'  and
  implementation 'com.squareup.okhttp3:okhttp:4.9.1'  
        Add this file: res/xml/network_security_config     
         In manifet file, in application tag, add:       
        android:usesCleartextTraffic="true"       
                  android:networkSecurityConfig="@xml/network_security_config"
      
