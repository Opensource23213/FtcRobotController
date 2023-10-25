package org.firstinspires.ftc.robotcontroller.teamcode;

/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

/*
  This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
  the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
  of the FTC Driver Station. When a selection is made from the menu, the corresponding OpMode
  class is instantiated on the Robot Controller and executed.

  This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
  It includes all the skeletal structure that all linear OpModes contain.

  Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
  Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Ronan_Mech", group="Linear Opmode")
//@Disabled
public class TyneyMechIVERSE extends LinearOpMode {

    // Declare OpMode members.
    IMU imu;
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor front_left = null;
    private DcMotor front_right = null;
    private DcMotor rear_left = null;
    private DcMotor rear_right = null;

    private DcMotor Arm1 = null;

    private DcMotor Arm2 = null;

    private DigitalChannel bouncer = null;

    private Servo elbow1;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        // Retrieve and initialize the IMU.
        imu = hardwareMap.get(IMU.class, "imu");
        front_left  = hardwareMap.get(DcMotor.class, "front_left");
        front_right = hardwareMap.get(DcMotor.class, "front_right");
        rear_left  = hardwareMap.get(DcMotor.class, "rear_left");
        rear_right = hardwareMap.get(DcMotor.class, "rear_right");
        Arm1 = hardwareMap.get(DcMotor.class, "Arm1");
        Arm2 = hardwareMap.get(DcMotor.class, "Arm2");
        bouncer = hardwareMap.get(DigitalChannel.class, "bouncer");
        elbow1 = hardwareMap.get(Servo.class, "elbow1");
        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        front_left.setDirection(DcMotor.Direction.REVERSE);
        front_right.setDirection(DcMotor.Direction.FORWARD);
        rear_left.setDirection(DcMotor.Direction.REVERSE);
        rear_right.setDirection(DcMotor.Direction.FORWARD);
        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rear_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rear_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Arm1.setDirection(DcMotor.Direction.FORWARD);
        Arm2.setDirection(DcMotor.Direction.FORWARD);
        Arm1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Arm1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Arm2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bouncer.setMode(DigitalChannel.Mode.INPUT);

        // Define hub orientation
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.FORWARD;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.UP;

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        // Initialize the IMU with this mounting orientation
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        telemetry.addData("Hub orientation", "Logo=%s   USB=%s\n ", logoDirection, usbDirection);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        imu.resetYaw();
        double max;
        double power_level = .5;
        double Pi = 3.1415926 / 2;
        int Drive_Mode = 0;
        double len1 = 36;
        double len2 = 22;
        double angle;
        double a;
        double b;


        double bpress = 1;
        double ypress = 1;
        double apress = 1;
        double rightpress = 1;
        double leftpress = 1;
        double uppress = 1;
        double downpress = 1;
        double xpress = 1;
        double reset = 2;
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double frontleftPower;
            double frontrightPower;
            double rearleftPower;
            double rearrightPower;
            double arm = gamepad2.left_stick_y/4;
            double ticks = 22.76; // ticks per degree
            boolean press = !bouncer.getState();
            if(gamepad2.left_stick_y < .05 && gamepad2.left_stick_y > -.05){
                arm = 0;
            }


            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;

            // Retrieve Rotational Angles and Velocities
            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
            AngularVelocity angularVelocity = imu.getRobotAngularVelocity(AngleUnit.DEGREES);

            // Check to see if heading reset is requested
            if (gamepad1.y) {
                telemetry.addData("Yaw", "Resetting\n");
                imu.resetYaw();
            } else {
                telemetry.addData("Yaw", "Press Y (triangle) on Gamepad to reset\n");
            }
            //Set Drive Mode
            double xPress = 1;
            if (gamepad1.x && xPress == 1) {
                Drive_Mode = 1;
                xPress = 2;
            }else if(gamepad1.x && xPress == 2) {
                Drive_Mode = 0;
                xPress = 1;
            }
            // max power level to limit speed

            if (gamepad1.left_bumper)
                power_level = 0.5;
            if (gamepad1.right_bumper)
                power_level = 1;
           // if (power_level > 1)
          //      power_level = 1;
          //  if (power_level < 0.1)
          //       power_level = 0.1;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.

            if (Drive_Mode == 1){
                double yaw_rad = orientation.getYaw(AngleUnit.RADIANS) + Pi;
                double temp = axial * Math.sin(yaw_rad) + lateral * Math.cos(yaw_rad);
                lateral = -axial * Math.cos(yaw_rad) + lateral * Math.sin(yaw_rad);
                //double temp = axial * Math.cos(yaw_rad) + lateral * Math.sin(yaw_rad);
                //lateral = -axial * Math.sin(yaw_rad) + lateral * Math.cos(yaw_rad);
                axial = temp;}

            double leftFrontPower = (axial + lateral + yaw) * power_level;
            double rightFrontPower = (axial - lateral - yaw) * power_level;
            double leftBackPower = (axial - lateral + yaw) * power_level;
            double rightBackPower = (axial + lateral - yaw) * power_level;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            // postion 1
           /* double tx = 58;
            double ty = 0;
            // follow
            if (b == 0) {
                double dir = len1 * -1;
                a = Arm1.getTargetPosition() + dir;
            }
*/
            /*if(xpress == 1) {
                if(reset == 1) {
                    Arm1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    Arm2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    sleep(50);
                    reset = 2;
                } else if (reset == 2) {
                    if (gamepad2.a){
                        if (apress < 3){
                            apress += 1;
                        } else if (apress >= 3) {
                            apress = 1;
                        }
                    }
                    if(apress == 1){
                        Arm1.setPower(arm);
                        Arm2.setPower(arm);
                        if (gamepad2.dpad_up){
                            if (uppress < 2){
                                uppress += 1;
                            } else if (uppress >= 2) {
                                uppress = 1;
                            }
                        }
                        if(uppress == 2){

                        }
                    }
                    if(apress == 2){
                        Arm1.setPower(arm);
                        Arm2.setPower(arm);
                        if (gamepad2.dpad_up){
                            if (uppress < 2){
                                uppress += 1;
                            } else if (uppress >= 2) {
                                uppress = 1;
                            }
                        }
                        if(uppress == 2){

                        }
                    }
                    if(apress == 3){
                        Arm1.setPower(arm);
                        Arm2.setPower(arm);
                        if (gamepad2.dpad_up){
                            if (uppress < 2){
                                uppress += 1;
                            } else if (uppress >= 2) {
                                uppress = 1;
                            }
                        }
                        if(uppress == 2){

                        }
                    }
                }
            }else if (xpress == 2){
                Arm1.setPower(arm);
                Arm2.setPower(arm);
                if (gamepad2.a){
                    if (apress < 3){
                        apress += 1;
                    } else if (apress >= 3) {
                        apress = 1;
                    }
                }
                if (gamepad2.x){
                    xpress = 1;
                    reset = 1;
                }
            }*/
            if(xpress == 1) {
                if(reset == 1) {
                    Arm1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    Arm2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    reset = 2;
                }
                if (reset == 2) {
                    if (Arm1.getCurrentPosition() > ticks * 90) {
                        if (gamepad2.left_stick_y > 0) {
                            Arm1.setPower(0);
                            Arm2.setPower(0);
                        } else {
                            Arm1.setPower(arm);
                            Arm2.setPower(arm);
                            if (gamepad2.x){
                                xpress = 2;
                            }
                        }
                    } else if (Arm1.getCurrentPosition() < ticks * -90) {
                        if (gamepad2.left_stick_y < 0) {
                            Arm1.setPower(0);
                            Arm2.setPower(0);
                        } else {
                            Arm1.setPower(arm);
                            Arm2.setPower(arm);
                            if (gamepad2.x){
                                xpress = 2;
                            }
                        }
                    } else {
                        Arm1.setPower(arm);
                        Arm2.setPower(arm);
                    }
                }
            }else if (xpress == 2){
                Arm1.setPower(arm);
                Arm2.setPower(arm);
                if (gamepad2.x){
                    xpress = 1;
                    reset = 1;
                }
            }

            if (max > 1.0) {
                leftFrontPower /= max;
                rightFrontPower /= max;
                leftBackPower /= max;
                rightBackPower /= max;
            }

            // Send calculated power to wheels
            front_left.setPower(leftFrontPower);
            front_right.setPower(rightFrontPower);
            rear_left.setPower(leftBackPower);
            rear_right.setPower(rightBackPower);

            //Arm code


            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Position", "1: Low, 2: Medium, 3: High " + apress);
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            telemetry.addData("Yaw (Z)", "%.2f Deg. (Heading)", orientation.getYaw(AngleUnit.DEGREES));
            telemetry.addData("Pitch (X)", "%.2f Deg.", orientation.getPitch(AngleUnit.DEGREES));
            telemetry.addData("Roll (Y)", "%.2f Deg.\n", orientation.getRoll(AngleUnit.DEGREES));
            telemetry.addData("Yaw (Z) velocity", "%.2f Deg/Sec", angularVelocity.zRotationRate);
            telemetry.addData("Pitch (X) velocity", "%.2f Deg/Sec", angularVelocity.xRotationRate);
            telemetry.addData("Roll (Y) velocity", "%.2f Deg/Sec", angularVelocity.yRotationRate);
            telemetry.addData("Shoulder Arm", "Angle: " + Arm1.getCurrentPosition()/ticks);
            telemetry.update();
            telemetry.update();
        }
    }
}
