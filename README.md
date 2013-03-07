# Totally and Causally ordered Group Messenger on Android
=========================================================
***
* The app multicasts every user-entered message to all app instances (including the one that is sending the message). In the rest of the description, “multicast” always means sending a message to all app instances.
* App uses **B-multicast**. It does not implement R-multicast.
* You need to come up with an algorithm that provides total-causal ordering.
* A content provider is implemented using SQLite on Android to store <key, value> pair.


- We have fixed the ports & sockets.
  - App opens one server socket that listens on 10000.
  - We will use up to 3 AVDs. The redirection ports are 11108, 11112, and 11116. All three ports are hard coded.

- Every message is stored in the provider individually by all app instances. Each message is stored as a <key, value> pair. The key should be the final delivery sequence number for the message (as a string); the value should be the actual message (again, as a string). The delivery sequence number should start from 0 and increase by 1 for each message.


## Test case 1

* Main Activity has “Test1” button that triggers the test case.
* When the button is clicked, it should create a thread that multicasts 5 messages in sequence. Multicasting of one message should be followed by 3 seconds sleep of the thread. This is just to make sure that messages can be spread from different emulator instances.
* The message format should be “<AVD name>:<sequence number>”. <AVD name> is the emulator instance’s name, e.g., avd0, avd1, and avd2. <sequence number> is a number starting from 0 and increasing by 1 for each message. For example, if your first emulator instance multicasts 5 messages, then the messages should be “avd0:0”, “avd0:1”, “avd0:2”, “avd0:3”, and “avd0:4”.
* With this test case, at least the following two ordering guarantees can be verified by just looking at the sequence of messages on each instance.
    * Total ordering: every emulator instance should display the same order of message
    * FIFO ordering: all messages from the same instance should preserve the local order. This should be preserved because causal ordering implies FIFO ordering.


## Test case 2

* Main Activity has “Test2” button that triggers the test case.
* When the button is clicked, app multicasts one message. The message format is the same as the above test case.
* Receiving of this first message triggers all app instances to multicast exactly two more messages. Unlike the test case 1, you should not introduce any extra delay between the messages.
    * Thus, in total with 3 AVDs, there should be 1 + 3 + 3 = 7 multicast messages.

## References

[1] Read about Multicast [here](http://www.cs.odu.edu/~cs778/jeffay/Lecture7.pdf)

[2] Single best resource on Android, [Android dev](http://developer.android.com)

[3] Message ordering, [here](http://www.cs.uic.edu/~ajayk/Chapter6.pdf)


## Screenshots

#### Test Case 1:

![Test Case-1](https://raw.github.com/adilansari/Group-Messenger/master/screen/test-1.png)

#### Test Case 2:

![Test Case-2](https://raw.github.com/adilansari/Group-Messenger/master/screen/test-2.png)
