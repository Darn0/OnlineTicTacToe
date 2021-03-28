package com.hope.onlinetictactoe;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.annotation.SuppressLint;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

// Import Firebase Analytics
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.analytics.FirebaseAnalytics;
// Import Firebase Auth
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText InvitePlayerEmail; // gets the opponent invite email from the UI
    EditText MyLoginEmail; // gets the local users email from the UI
    EditText MyLoginPassword; // gets the local user password
    Button buLogin; // UI login button
    Button buInvite; // UI invite button
    Button buAccept; // UI accept button
    Button buRegister; // UI Register button
    Button buLogout; // UI Logout button



    // Firebase Analytics
    private FirebaseAnalytics mFirebaseAnalytics;
    //Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // String for Email
    String MyEmail;
    //string for uid handling incoming user requests
    String uid;
    // Write a message to the database
    // get instance with the location of database as a path since its not the default US one
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://onlinetictactoe-b6308-default-rtdb.europe-west1.firebasedatabase.app");// enter you firebase realtime database instance URL here
    DatabaseReference myRef = database.getReference(); // get the reference to pass back to firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Firebase Auth
        mAuthListener = new FirebaseAuth.AuthStateListener() { // new Firebase auth listener
            private static final String TAG = "Auth Activity"; // tag for auth activity
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser(); // get current user
                if (user != null) {
                    //User has signed in
                    uid = user.getUid();
                    Log.d(TAG, "Auth State Changed - Signed In: " + uid); // log user sign in
                    MyEmail = user.getEmail(); // get the users email address and assign it to MyEmail
                    buLogin.setEnabled(false);// disable the login button if the user is already logged in
                    MyLoginEmail.setText(MyEmail); // set the local user login to their email address
                    MyLoginEmail.setEnabled(false); // disable the local users email text input
                    MyLoginPassword.setText("Password is hidden"); // fill the user password with text
                    MyLoginPassword.setEnabled(false); // disable the users password input
                    // save the users email name into the database calling the BeforeAt function splitting the email before the @ symbol and the user request id
                    myRef.child("Users").child(BeforeAt(MyEmail)).child("Requests").setValue(uid);//set the user id that has been made public
                    //myRef.setValue("Hello, World!");
                    IncomingRequest();// setup the incoming game requests
                    buLogin.setBackgroundColor(Color.GRAY);// set login button to grey
                    buRegister.setEnabled(false);// disable register button if already logged in
                    buRegister.setBackgroundColor(Color.GRAY);// set the register button grey
                } else {
                    //User has signed out
                    Log.d(TAG, "Auth State Changed - Signed Out: "); // log user sign out

                }
            }
        };

        InvitePlayerEmail = findViewById(R.id.InvitePlayerEmail); // initilize the the opponent email from the UI
        MyLoginEmail = findViewById(R.id.MyLoginEmail); // initilize the local users email from the UI
        MyLoginPassword = findViewById(R.id.MyLoginPassword); // initilize the local user password from the UI
        buLogin = findViewById(R.id.buLogin); // initilize the Login button form the UI
        buInvite = findViewById(R.id.buInvite); // initilize the Invite button form the UI
        buAccept = findViewById(R.id.buAccept); // initilize the Accept button form the UI
        buRegister = findViewById(R.id.buRegister); // initilize the Register button from the UI
        buLogout = findViewById(R.id.buLogout); // initilize the Logout button from the UI


    }
    //firebase won't accept @ so just save the users first part of their email
    String BeforeAt (String Email) {
        String[] split = Email.split("@"); // split the email address before the @
        return split[0];
    }


    //Firebase Auth Login
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);// on start add auth state listener
        buAccept.setEnabled(false); // disable the accept button as there is no need for it to be enabled without an incoming request
        buAccept.setBackgroundColor(Color.GRAY); // set the button to grey so it is visually not enabled to the user
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    //Firebase Auth Reg Handler
    void UserRegistration(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    private static final String TAG = "Registration";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "Create User With Email : OnComplete :" + task.isSuccessful());

                        if (!task.isSuccessful()) { // if the task is not successful show a toast message informing the user
                            Toast.makeText(getApplicationContext(), "Registration Failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void UserLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    private static final String TAG = "Login";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Login Failed!",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }
                    }
                });
    }

    public void buInvite(View view) { // handles the button click for Invite
        Log.d("Invite", InvitePlayerEmail.getText().toString()); // log the output when the Invite button is pressed
        if (InvitePlayerEmail.length() != 0) {// check the email length is not 0
            // Reset the game board
            findViewById(R.id.bu1).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu2).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu3).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu4).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu5).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu6).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu7).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu8).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu9).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu1).setEnabled(true);
            findViewById(R.id.bu2).setEnabled(true);
            findViewById(R.id.bu3).setEnabled(true);
            findViewById(R.id.bu4).setEnabled(true);
            findViewById(R.id.bu5).setEnabled(true);
            findViewById(R.id.bu6).setEnabled(true);
            findViewById(R.id.bu7).setEnabled(true);
            findViewById(R.id.bu8).setEnabled(true);
            findViewById(R.id.bu9).setEnabled(true);
            //send request to a user via the invitePlayerEmail input and MyEmail so the other user knows who the request is from, push will assign a random ID to the request
            myRef.child("Users").child(BeforeAt(InvitePlayerEmail.getText().toString())).child("Requests").push().setValue(MyEmail);
            StartGame(BeforeAt(InvitePlayerEmail.getText().toString()) + ":" + BeforeAt(MyEmail));
            MySample = "X";//when invite set player as X
            ReplayGame = "Invite"; // if the game is replayed between two players make sure to play how the game was originaly set up
        } else {
            Toast.makeText(this, "Please enter an Invite email address", Toast.LENGTH_LONG).show();
        }
    }

    void IncomingRequest() {
        // Read from the database
        myRef.child("Users").child(BeforeAt(MyEmail)).child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    HashMap<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue(); // create a hash map of the requests using casting and getValues as we are using key pairs
                    if (td != null) { // if the is a request that is not null

                        String value;
                        for(String key:td.keySet()){ // for each string key in the key set (i.e. get all the requests to the local user)
                            value =(String)td.get(key);// get and assign the values and convert to string
                            Log.d("User Request",value);// log the request
                            InvitePlayerEmail.setText(value); // set the input as the incoming players email as to identify them
                            IncomingAlert();// trigger the incoming alert
                            // InvitePlayerEmail.setBackgroundColor(Color.GREEN);
                            myRef.child("Users").child(BeforeAt(MyEmail)).child("Requests").setValue(uid);// send the local users email and thier unique ID back to firebase
                            break;//break request
                        }
                    }
                }catch (Exception ignored) {}//catch any exceptions
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void IncomingAlert() {// flashes the Invitation input to alert the user to an incoming request
        InvitePlayerEmail.setBackgroundColor(Color.rgb(76,175,80));// set background colour to green to alert to incoming request
        buAccept.setEnabled(true);// enable the accept button
        buAccept.setBackgroundColor(Color.rgb(76,175,80));// set background colour for the alert button to green to alert to incoming request
        Toast.makeText(this,"Incoming Request From: " + InvitePlayerEmail.getText().toString(),Toast.LENGTH_SHORT).show();

    }
    String ReplayGame = "";
    public void buAccept(View view) { // handles the button click for Accept
        Log.d("Accept", InvitePlayerEmail.getText().toString()); // log the output when the Accept button is pressed
        if (InvitePlayerEmail.length() != 0 && InvitePlayerEmail.length() > 3) {// check the email is not 0 and length grater than 3
            buAccept.setEnabled(false); // disable the button whilst playing another player
            InvitePlayerEmail.setBackgroundColor(Color.WHITE);// reset background colour on new request
            //send request to a user via the invitePlayerEmail input and MyEmail so the other user knows who the request is from,
            // push will assign a random ID to the request this creates a response to the initial game request
            myRef.child("Users").child(BeforeAt(InvitePlayerEmail.getText().toString())).child("Requests").push().setValue(MyEmail);
            StartGame(BeforeAt(MyEmail) + ":" + BeforeAt(InvitePlayerEmail.getText().toString()));//start the game with the name of the two players
            MySample = "O";// when game accept set as O
            ReplayGame = "Accept"; // if the game is replayed between two players make sure to play how the game was originaly set up
            //Reset the game board
            findViewById(R.id.bu1).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu2).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu3).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu4).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu5).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu6).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu7).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu8).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu9).setBackgroundColor(Color.BLACK);
            findViewById(R.id.bu1).setEnabled(true);
            findViewById(R.id.bu2).setEnabled(true);
            findViewById(R.id.bu3).setEnabled(true);
            findViewById(R.id.bu4).setEnabled(true);
            findViewById(R.id.bu5).setEnabled(true);
            findViewById(R.id.bu6).setEnabled(true);
            findViewById(R.id.bu7).setEnabled(true);
            findViewById(R.id.bu8).setEnabled(true);
            findViewById(R.id.bu9).setEnabled(true);
            Toast.makeText(this,"Game Started With: " + InvitePlayerEmail.getText().toString(),Toast.LENGTH_SHORT).show();
        }
    }


    public void buLogin(View view) { // handles button click for Login
        Log.d("Login", MyLoginEmail.getText().toString()); // log the output when the Login button is pressed of the user email
        Log.d("Password", MyLoginPassword.getText().toString()); // log the output when the login button is pressed of the user password
        if (MyLoginEmail.length() != 0 && MyLoginPassword.length() !=0) {// check the user name and password length are not 0
            UserLogin(MyLoginEmail.getText().toString(), MyLoginPassword.getText().toString());
        } else { Toast.makeText(this, "Please Enter Your Email and Password!",
                Toast.LENGTH_LONG).show();
        }

    }

    public void buLogout(View view) { // handles logging out when the logout button is pressed
        FirebaseAuth.getInstance().signOut(); // sign out of firebase
        buLogin.setEnabled(true); // re-enable the login button
        MyLoginEmail.setEnabled(true); // re-enable the local user text input
        MyLoginPassword.setEnabled(true);// re-enable the local password input
        MyLoginPassword.setText("");//set password text to blank
        buRegister.setEnabled(true);// re-enable the register button
        buLogin.setBackgroundColor(Color.rgb(76,175,80));// set button colour back to green for login button
        buRegister.setBackgroundColor(Color.rgb(136,0,255)); // set button color back to its original
    }

    public void buRegister(View view) {// handles registering a user
        Log.d("Login", MyLoginEmail.getText().toString()); // log the output when the register button is pressed of the user email
        Log.d("Password", MyLoginPassword.getText().toString()); // log the output when the register button is pressed of the user password
        if (MyLoginEmail.length() != 0 && MyLoginPassword.length() !=0) {// check the user name and password length are not 0
            UserRegistration(MyLoginEmail.getText().toString(), MyLoginPassword.getText().toString());
        } else { Toast.makeText(this,"Please Enter Your Registration Details",Toast.LENGTH_LONG).show();
        }
    }
    String MySample ="X";// my Game Char
    String PlayerSession = "";//set and init PlayerSession as blank
    void StartGame(String PlayerGameID) {// starts the multi player game with a remote player
        PlayerSession = PlayerGameID;//set player session to the PlayerGameID
        myRef.child("Playing").child(PlayerGameID).removeValue();// assigns the players to the correct game ID by assigning them to the same node
        // Read from the database
        myRef.child("Playing").child(PlayerGameID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        try{
                            Player1.clear();// clear player 1 for a new game
                            Player2.clear();// clear player 2 for a new game
                            ActivePlayer=2;// active player will be two so 2 set here
                            HashMap<String,Object> td=(HashMap<String,Object>) dataSnapshot.getValue();
                            if (td!=null){

                                String value;

                                for(String key:td.keySet()){
                                    value=(String) td.get(key);
                                    assert value != null;
                                    if(!value.equals(BeforeAt(MyEmail)))
                                        ActivePlayer= MySample.equals("X") ?1:2;
                                    else
                                        ActivePlayer= MySample.equals("X") ?2:1;

                                    String[] splitID= key.split(":");
                                    AutoPlay(Integer.parseInt(splitID[1]));

                                }
                            }


                        }catch (Exception ignored){}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    public void BuClick(View view) {// this is responsible for handling the Game play button presses
        Log.d("Game", "Game Button Clicked"); // log the output when the Login button is pressed
        // game not started yet
        if (PlayerSession.length() <= 0) // if no players then return
            return;

        Button buSelected = (Button) view;
        int CellID = 0;
        int id = (buSelected.getId());// use a switch to select the corrosponding cellID to the button pressed
        if (id == R.id.bu1) {
            CellID = 1;
        } else if (id == R.id.bu2) {
            CellID = 2;
        } else if (id == R.id.bu3) {
            CellID = 3;
        } else if (id == R.id.bu4) {
            CellID = 4;
        } else if (id == R.id.bu5) {
            CellID = 5;
        } else if (id == R.id.bu6) {
            CellID = 6;
        } else if (id == R.id.bu7) {
            CellID = 7;
        } else if (id == R.id.bu8) {
            CellID = 8;
        } else if (id == R.id.bu9) {
            CellID = 9;
        }
        myRef.child("Playing").child(PlayerSession).child( "CellID:"+ CellID).setValue(BeforeAt(MyEmail));// push the cellID selection and email to Firebase
    }

    int ActivePlayer=1; // 1 for first , 2 for second
    ArrayList<Integer> Player1= new ArrayList<>();// hold player 1 data
    ArrayList<Integer> Player2= new ArrayList<>();// hold player 2 data
    void PlayGame(int CellID,Button buSelected){// called when the PlayGame function is run

        Log.d("Player:",String.valueOf(CellID));// log the cellID each player presses

        switch (ActivePlayer) {
            case 1:
                buSelected.setText("X");// change player 1 selection to X

                buSelected.setBackgroundColor(Color.GREEN);//change player 1 tile to green

                Player1.add(CellID);//add cell id to player 1


                break;
            case 2:
                buSelected.setText("O");// change player 2 selection to O

                buSelected.setBackgroundColor(Color.BLUE);//change player 2 tile to blue

                Player2.add(CellID);// add cell id to player 2


                break;
        }
        buSelected.setEnabled(false);// disable the button from being selectable
        CheckWinner();// run the check winners function

    }

    void CheckWinner(){// check the board against pre-set conditions for winning the game
        int Winner=-1;
        //row 1
        if (Player1.contains(1) && Player1.contains(2)  && Player1.contains(3)) Winner = 1;
        if (Player2.contains(1) && Player2.contains(2)  && Player2.contains(3)) Winner = 2;

        //row 2
        if (Player1.contains(4) && Player1.contains(5)  && Player1.contains(6)) Winner = 1;
        if (Player2.contains(4) && Player2.contains(5)  && Player2.contains(6)) Winner = 2;

        //row 3
        if (Player1.contains(7) && Player1.contains(8)  && Player1.contains(9)) Winner = 1;
        if (Player2.contains(7) && Player2.contains(8)  && Player2.contains(9)) Winner = 2;

        //col 1
        if (Player1.contains(1) && Player1.contains(4)  && Player1.contains(7)) Winner = 1;
        if (Player2.contains(1) && Player2.contains(4)  && Player2.contains(7)) Winner = 2;

        //col 2
        if (Player1.contains(2) && Player1.contains(5)  && Player1.contains(8)) Winner = 1;
        if (Player2.contains(2) && Player2.contains(5)  && Player2.contains(8)) Winner = 2;

        //col 3
        if (Player1.contains(3) && Player1.contains(6)  && Player1.contains(9)) Winner = 1;
        if (Player2.contains(3) && Player2.contains(6)  && Player2.contains(9)) Winner = 2;

        //cross
        if (Player1.contains(3) && Player1.contains(7)  && Player1.contains(5)) Winner = 1;
        if (Player2.contains(3) && Player2.contains(7)  && Player2.contains(5)) Winner = 2;

        //cross 2
        if (Player1.contains(1) && Player1.contains(5)  && Player1.contains(9)) Winner = 1;
        if (Player2.contains(1) && Player2.contains(5)  && Player2.contains(9)) Winner = 2;

        if ( Winner !=-1){
            // We have winner
            int P1Wins = 0;
            int P2Wins = 0;
            if (Winner == 1) {
                Toast.makeText(this,"Player 2 is winner\nClick the tiles to play again\nPlayer1 "+ P1Wins +":"+ P2Wins +" Player2",Toast.LENGTH_SHORT).show();
                Player1.clear();
                Player2.clear();

                if (ReplayGame.equals("Accept")) {
                    //send request to a user via the invitePlayerEmail input and MyEmail so the other user knows who the request is from,
                    // push will assign a random ID to the request this creates a response to the initial game request
                    myRef.child("Users").child(BeforeAt(InvitePlayerEmail.getText().toString())).child("Requests").push().setValue(MyEmail);
                    StartGame(BeforeAt(MyEmail) + ":" + BeforeAt(InvitePlayerEmail.getText().toString()));//start the game with the name of the two players
                    MySample = "O";// when game accept set as O
                } else if (ReplayGame.equals("Invite")) {
                    //send request to a user via the invitePlayerEmail input and MyEmail so the other user knows who the request is from, push will assign a random ID to the request
                    myRef.child("Users").child(BeforeAt(InvitePlayerEmail.getText().toString())).child("Requests").push().setValue(MyEmail);
                    StartGame(BeforeAt(InvitePlayerEmail.getText().toString()) + ":" + BeforeAt(MyEmail));
                    MySample = "X";//when invite set player as X
                }
                //Reset the game board
                findViewById(R.id.bu1).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu2).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu3).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu4).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu5).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu6).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu7).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu8).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu9).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu1).setEnabled(true);
                findViewById(R.id.bu2).setEnabled(true);
                findViewById(R.id.bu3).setEnabled(true);
                findViewById(R.id.bu4).setEnabled(true);
                findViewById(R.id.bu5).setEnabled(true);
                findViewById(R.id.bu6).setEnabled(true);
                findViewById(R.id.bu7).setEnabled(true);
                findViewById(R.id.bu8).setEnabled(true);
                findViewById(R.id.bu9).setEnabled(true);
                P1Wins++;
            }

            if (Winner==2){
                Toast.makeText(this,"Player 2 is winner\nClick the tiles to play again\nPlayer1 "+ P1Wins +":"+ P2Wins +" Player2",Toast.LENGTH_SHORT).show();
                Player1.clear();
                Player2.clear();

                if (ReplayGame.equals("Accept")) {
                    //send request to a user via the invitePlayerEmail input and MyEmail so the other user knows who the request is from,
                    // push will assign a random ID to the request this creates a response to the initial game request
                    myRef.child("Users").child(BeforeAt(InvitePlayerEmail.getText().toString())).child("Requests").push().setValue(MyEmail);
                    StartGame(BeforeAt(MyEmail) + ":" + BeforeAt(InvitePlayerEmail.getText().toString()));//start the game with the name of the two players
                    MySample = "O";// when game accept set as O
                } else if (ReplayGame.equals("Invite")) {
                    //send request to a user via the invitePlayerEmail input and MyEmail so the other user knows who the request is from, push will assign a random ID to the request
                    myRef.child("Users").child(BeforeAt(InvitePlayerEmail.getText().toString())).child("Requests").push().setValue(MyEmail);
                    StartGame(BeforeAt(InvitePlayerEmail.getText().toString()) + ":" + BeforeAt(MyEmail));
                    MySample = "X";//when invite set player as X
                }
                //reset the game board
                findViewById(R.id.bu1).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu2).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu3).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu4).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu5).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu6).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu7).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu8).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu9).setBackgroundColor(Color.BLACK);
                findViewById(R.id.bu1).setEnabled(true);
                findViewById(R.id.bu2).setEnabled(true);
                findViewById(R.id.bu3).setEnabled(true);
                findViewById(R.id.bu4).setEnabled(true);
                findViewById(R.id.bu5).setEnabled(true);
                findViewById(R.id.bu6).setEnabled(true);
                findViewById(R.id.bu7).setEnabled(true);
                findViewById(R.id.bu8).setEnabled(true);
                findViewById(R.id.bu9).setEnabled(true);
                P2Wins++;
            }

        }

    }

    void AutoPlay(int CellID){
        Button buSelected;
        switch (CellID){
            case 2:
                buSelected= findViewById(R.id.bu2);
                break;
            case 3:
                buSelected= findViewById(R.id.bu3);
                break;
            case 4:
                buSelected= findViewById(R.id.bu4);
                break;
            case 5:
                buSelected= findViewById(R.id.bu5);
                break;
            case 6:
                buSelected= findViewById(R.id.bu6);
                break;
            case 7:
                buSelected= findViewById(R.id.bu7);
                break;
            case 8:
                buSelected= findViewById(R.id.bu8);
                break;
            case 9:
                buSelected= findViewById(R.id.bu9);
                break;
            default:
                buSelected= findViewById(R.id.bu1);
                break;

        }
        PlayGame(CellID, buSelected);
    }
}