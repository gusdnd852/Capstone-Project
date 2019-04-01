import io
import socket
import struct
import time
import picamera
import threading
from PIL import Image

# Connect a client socket to my_server:8000 (change my_server to the
# hostname of your server)




def stream():
 
 try: 
    client_socket = socket.socket()
    print("loop")
    client_socket.connect(('27.116.98.204', 9893))
    # Make a file-like object out of the connection
    connection = client_socket.makefile('wb')
    while(True):
     try:
        with picamera.PiCamera() as camera:
            if(client_socket == None):
                client_socket = socket.socket()
                client_socket.connect(('27.116.98.204', 9893))
                connection = client_socket.makefile('wb')

            camera.resolution = (640, 480)
            # Start a preview and let the camera warm up for 2 seconds
            camera.start_preview()
            # Note the start time and construct a stream to hold image data
            # temporarily (we could write it directly to connection but in this
            # case we want to find out the size of each capture first to keep
            # our protocol simple)
            
            start = time.time()
            stream = io.BytesIO()
            for foo in camera.capture_continuous(stream, 'jpeg'):
                # Write the length of the capture to the stream and flush to
                # ensure it actually gets sent
                
                time.sleep(0.5)
                image=Image.open(stream)
                image.save("face.jpg")
                print("saveImage")

#print("test"+stream)
                connection.write(struct.pack('<L', stream.tell()))
                connection.flush()
                # Rewind the stream and send the image data over the wire
                stream.seek(0)
                connection.write(stream.read())
                # If we've been capturing for more than 30 seconds, quit
              
                # Reset the stream for the next capture
                stream.seek(0)
                stream.truncate()
                
    # Write a length of zero to the stream to signal we're done
        connection.write(struct.pack('<L', 0))
        
     except:
         connection = None
         client_socket =None
         print("exception")

 except:
    print("ecep")
streaming = threading.Thread(target=stream)
#face_thread = threading.Thread(target=faceDetect,args=(emotion_judge,emotion_late))
#face_thread.start()
streaming.start()
