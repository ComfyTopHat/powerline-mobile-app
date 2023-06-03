## Powerline App Dev Log / To-Do List

//== 03/06/2023 ==//

BEN:

# DONE
- Create base application for Android (currently in Java)
- Create API toolbox for easier handling of data getting/sending
- Create custom Message Data List for displaying in Recycler view (to allow for scrolling and dynamic links)
- Create custom Contact data list for same
- Create login menu
- Added create account with dynamic generation
- Added contact retrieval 
- Added dynamic message contact threads based on most recent messages (either sent to logged in user or sent by logged in user)
- Add login screen password handling and display error on incorrect user/pass
- Added multithreading for network and API actions
- 


# TODO
- Enable client side (assymetric) text and pword hashing (this will require some updates to the API logic)
- Clean up message thread and contact icons
- Select a font/theme for application
- Add appropriate error/exception handling 
- Convert MessageDataList handling from an array to a LinkedList. This is to allow for message search and easier removal of data
- Update the send message function to a Notify/Listener to avoid a second API call to refresh messages
- Integration Firebase SDK for notification handling
- Move network response retreival to its own function in the API toolbox, currently there is duplicate code scattered throughout
- Clean up the handling of shared data (such as logged in clientID, username and clientID/username of contacts)
- Make another custom adapter to display conversations on first login (this needs to be seperate from existing Message/Contact datalists)
- Clean up variable naming conventions across classes

//================//
