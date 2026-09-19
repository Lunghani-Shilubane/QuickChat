/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.project1;
import java.util.Scanner;
/**
 *
 * @author bigbe
 */
public class Project1 {

    public static void main(String[] args) {
       
        
        Scanner usrName = new Scanner(System.in);
        System.out.println("Please write your username");
        String userName = usrName.nextLine();
        if(userName.length() > 5 && userName.contains("_")){
            System.out.println("User name successfully captured");
        }else{
            System.out.println("User name is not correctly formatted; please ensure that your username contains an underscore and is no longer than 5 characters long.");
            return;
        }
        
        Scanner psword = new Scanner(System.in);
        System.out.println("Please write your password");
        String password = psword.nextLine();
        if(password.length() <= 8 && password.matches(".*[A-Z]*.") && password.matches(".*\\d.*") && password.matches(".*[^a-zA-z0-9].*")){
            System.out.println("Password successfully captured");
        }else{
            System.out.println("Password is not correctly formatted; please make sure that the password contains at least 8 characters, a capital letter, and a spacial character.");
            return;
        }
        
        
        Scanner pnmbr = new Scanner(System.in);
        System.out.println("Please write your phone number");
        String phoneNumber = pnmbr.nextLine();
        if(phoneNumber.contains("+27") && phoneNumber.length() > 10){
            System.out.println("Cell phone number successfully added");
        } else{
            System.out.println("Cell phone number incorrectly formatted or does not contain international code");
            return;
        }
         
        System.out.println("Your name is:" + userName + "Your password is:" + password + "Your number is:" + phoneNumber);
    }
}
