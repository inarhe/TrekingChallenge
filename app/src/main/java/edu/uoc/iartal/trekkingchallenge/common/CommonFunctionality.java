package edu.uoc.iartal.trekkingchallenge.common;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.challenge.ListChallengesActivity;
import edu.uoc.iartal.trekkingchallenge.group.ListGroupsActivity;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.map.MapActivity;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.route.ListRoutesActivity;
import edu.uoc.iartal.trekkingchallenge.trip.ListTripsActivity;
import edu.uoc.iartal.trekkingchallenge.user.UserAreaActivity;
import edu.uoc.iartal.trekkingchallenge.user.UserHistoryActivity;


// Functionality used by different activities
public class CommonFunctionality {

    /**
     *  Validate mail format
     * @param email
     * @return if mail is ok
     */
    public boolean validateEmail(String email) {
        Pattern pattern;

        // Define mail pattern
        pattern = Pattern.compile(ConstantsUtils.EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Validate password length
     * @param password
     * @return if password is ok
     */
    public boolean validatePassword(String password) {
        return password.length() > 5;
    }

    /**
     *  Validate distance format
     * @param distance
     * @return if mail is ok
     */
    public boolean validateDistance(String distance) {
        Pattern pattern;

        // Define distance pattern
        pattern = Pattern.compile(ConstantsUtils.DISTANCE_PATTERN);
        Matcher matcher = pattern.matcher(distance);
        return matcher.matches();
    }

    /**
     * Sum hours and minutes in time format
     * @param historyHour
     * @param hour
     * @param historyMin
     * @param min
     * @return array with hours and minutes
     */
    public ArrayList<Integer> sumTime (int historyHour, int hour, int historyMin, int min){
        ArrayList<Integer> time = new ArrayList<>();
        int totalMin = historyMin + min;
        int countHour = 0;

        while (totalMin >= 60){
            totalMin = totalMin - 60;
            countHour ++;
        }

        time.add(historyHour + hour + countHour);
        time.add(totalMin);

        return time;
    }

    /**
     * Round double number decimals
     * @param value
     * @param places
     * @return
     */
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Start main activity when navigation drawer option is clicked
     */
    public void startHomeNavigationDrawer(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * Start map activity when navigation drawer option is clicked
     */
    public void startMapNavigationDrawer(Context context){
        Intent intent = new Intent(context, MapActivity.class);
        context.startActivity(intent);
    }

    /**
     * Start route activity when navigation drawer option is clicked
     */
    public void startRouteNavigationDrawer(Context context){
        Intent intent = new Intent(context, ListRoutesActivity.class);
        context.startActivity(intent);
    }

    /**
     * Start trip activity when navigation drawer option is clicked
     */
    public void startTripNavigationDrawer(Context context){
        Intent intent = new Intent(context, ListTripsActivity.class);
        context.startActivity(intent);
    }

    /**
     * Start challenge activity when navigation drawer option is clicked
     */
    public void startChallengeNavigationDrawer(Context context){
        Intent intent = new Intent(context, ListChallengesActivity.class);
        context.startActivity(intent);
    }

    /**
     * Start user activity when navigation drawer option is clicked
     */
    public void startUserNavigationDrawer(Context context){
        Intent intent = new Intent(context, UserAreaActivity.class);
        context.startActivity(intent);
    }

    /**
     * Start map activity when navigation drawer option is clicked
     */
    public void startGroupNavigationDrawer(Context context){
        Intent intent = new Intent(context, ListGroupsActivity.class);
        context.startActivity(intent);
    }

    /**
     * Logout when navigation drawer option is clicked
     */
    public void startLogOutNavigationDrawer(final Context context){
        final FirebaseController controller = new FirebaseController();
        DatabaseReference databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        controller.readDataOnce(databaseUser, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                String currentMail = controller.getCurrentUserEmail();

                for (DataSnapshot userSnapshot : data.getChildren()){
                    User user = userSnapshot.getValue(User.class);

                    if (user.getMail().equals(currentMail)){
                        controller.signOutDatabase(context);
                        break;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("LogOut error", databaseError.getMessage());
            }
        });
    }

    /**
     * Encrypt password to protect user information
     * @param password
     * @return
     * @throws Exception
     */
    public String encryptPassword(String password) throws Exception{
        SecretKeySpec key = generateKey(ConstantsUtils.KEY_CIPHER);
        Cipher c = Cipher.getInstance(ConstantsUtils.ALGORITHM_AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(password.getBytes());
        String encryptedPassword = Base64.encodeToString(encValue, Base64.DEFAULT);

        return encryptedPassword;
    }

    /**
     * Decrypt user password
     * @param password
     * @return
     * @throws Exception
     */
    public String decryptPassword(String password) throws Exception{

        SecretKeySpec key = generateKey(ConstantsUtils.KEY_CIPHER);
        Cipher c = Cipher.getInstance(ConstantsUtils.ALGORITHM_AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(password, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);

        return decryptedValue;
    }

    /**
     * Generaye a key from a given password
     * @param password
     * @return
     * @throws Exception
     */
    private SecretKeySpec generateKey(String password) throws Exception{

        final MessageDigest digest = MessageDigest.getInstance(ConstantsUtils.ALGORITHM_SHA_256);
        byte[] bytes = password.getBytes(ConstantsUtils.UTF_8);
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ConstantsUtils.ALGORITHM_AES);

        return secretKeySpec;
    }
}
