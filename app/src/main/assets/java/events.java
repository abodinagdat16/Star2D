            @Override
			public void onClick(PlayerItem current) {
			if(current.getProperties().getScript()!=null)
			current.getProperties().getScript().onClick();
%1$s
			}

			@Override
			public void onTouchStart(PlayerItem current,MotionEvent event) {
				if(current.getProperties().getScript()!=null)
			current.getProperties().getScript().onTouchStart(event);
%2$s
			}

			@Override
			public void onTouchEnd(PlayerItem current, MotionEvent event) {
				if(current.getProperties().getScript()!=null)
			current.getProperties().getScript().onTouchEnd(event);
%3$s
			}

			@Override
			public void onBodyCreated(PlayerItem current) {
				if(current.getProperties().getScript()!=null)
			current.getProperties().getScript(). onBodyCreated();
%4$s
			}

            @Override
            public void onBodyUpdate(PlayerItem current){
            	if(current.getProperties().getScript()!=null)
			current.getProperties().getScript().onBodyUpdate();
%5$s
            }
            
			@Override
			public void onCollisionBegin(PlayerItem current, PlayerItem body2) {
				if(current.getProperties().getScript()!=null)
			current.getProperties().getScript().onCollisionBegin(body2);
%6$s
           }
            
			@Override
			public void onCollisionEnd(PlayerItem current, PlayerItem body2) {
				if(current.getProperties().getScript()!=null)
			current.getProperties().getScript().onCollisionEnd(body2);
%7$s
			}
			
			@Override
			public String getName(){
			    %8$s
			}