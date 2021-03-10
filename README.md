# shedlock-demo
This project shows the implementation of shedlock for distributed services in spring boot.
We have configured our scheduler method using shedlock in such way so that even for the distributed 
service, only one scheduler of the same type will be executed at the same time, having the exact 
delay time we set in the app. Please see shedlock details here
https://github.com/lukas-krecan/ShedLock