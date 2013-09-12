akka-test
=========

Testing Akka.

Compile the project with:
------------------

    $ mvn assembly:assembly

Run the project with:
------------------

HelloWorld:

    $ java -cp target/akka-test-1.0-SNAPSHOT-jar-with-dependencies.jar akka.Main hu.ebratanki.HelloWorld

Ring measurement (where 1000000 is the number of actors and 2 is how many times to send the messages through the ring):

    $ time java -cp target/akka-test-1.0-SNAPSHOT-jar-with-dependencies.jar hu.ebratanki.ring.Ring 1000000 2
