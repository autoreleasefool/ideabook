package ca.josephroque.idea.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import ca.josephroque.idea.Assets;
import ca.josephroque.idea.Data;


public class Notification implements Runnable {
	
	
	public static final int NOTIF_ICON_INFORMATION = 0;
	public static final int NOTIF_ICON_TIP = 1;
	public static final int NOTIF_ICON_ERROR = 2;
	
	public static final int NOTIF_PRIORITY_MINOR = 0;
	public static final int NOTIF_PRIORITY_DEFAULT = 1;
	public static final int NOTIF_PRIORITY_URGENT = 2;
	
	private static final int QUEUE_CAPACITY = 5;

	private static Thread thread;
	private static Timer notificationTimer;
	private static LinkedBlockingQueue<String> notificationQueue;
	private static LinkedBlockingQueue<Integer> iconQueue;
	private static AtomicReferenceArray<NotificationWindow> windowArray;
	private static NotificationWindow currentWindow;
	private static final AtomicInteger currentNotificationCount = new AtomicInteger();
	private static final AtomicBoolean notificationQueueFailed = new AtomicBoolean(false);
	private static final AtomicBoolean[] isNotificationPositionFilled = new AtomicBoolean[QUEUE_CAPACITY];
	
	private static boolean threadInitialized = false;
	
	private Notification() {
		notificationQueue = new LinkedBlockingQueue<String>(QUEUE_CAPACITY);
		iconQueue = new LinkedBlockingQueue<Integer>(QUEUE_CAPACITY);
		windowArray = new AtomicReferenceArray<NotificationWindow>(QUEUE_CAPACITY);
		notificationTimer = null;
		
		for (int i = 0; i<QUEUE_CAPACITY; i++) {
			isNotificationPositionFilled[i] = new AtomicBoolean(false);
		}
		
		threadInitialized = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public static void beginThread() {
		new Notification();
	}
	
	public static void queueErrorNotification(String message) {queueNotification(message, NOTIF_PRIORITY_URGENT, NOTIF_ICON_ERROR);}
	public static void queueInformationNotification(String message) {queueNotification(message, NOTIF_PRIORITY_DEFAULT, NOTIF_ICON_INFORMATION);}
	public static void queueTipNotification(String message) {queueNotification(message, NOTIF_PRIORITY_MINOR, NOTIF_ICON_TIP);}
	
	public static void queueNotification(String message, int priority, int icon) {
		if (!threadInitialized)
			return;
		
		if (message.length() > 75) {
			System.out.println("Message too long: " + message);
		}
		
		if (!notificationQueueFailed.get()) {
			try {
				if (notificationQueue.size() < QUEUE_CAPACITY) {
					notificationQueue.put(message);
					iconQueue.put(new Integer(icon));
				}
			} catch (InterruptedException e) {
				Data.printErrorMessage(e);
				notificationQueueFailed.set(true);
			}
		}
	}
	
	public void run() {
		String nextNotification = null;
		Integer nextIcon = null;
		
		while (true) {
			try {
				nextNotification = notificationQueue.take();
				nextIcon = iconQueue.take();
				
				label:
				for (int i = 0; i<QUEUE_CAPACITY; i++) {
					if (!isNotificationPositionFilled[i].get()) {
						createNewPopup(new String(nextNotification), nextIcon.intValue(), 0);
						createNewPopup(new String(nextNotification), nextIcon.intValue(), 1);
						isNotificationPositionFilled[i].set(true);
						break label;
					}
				}
			} catch (InterruptedException e) {
				Data.printErrorMessage(e);
				notificationQueueFailed.set(true);
			}
		}
	}
	
	private void createNewPopup(String message, int icon, int position) {
		currentNotificationCount.getAndIncrement();
		NotificationWindow window = new NotificationWindow(message, icon, position);
		
		exitLoop:
		for (int i = 0; i<windowArray.length(); i++) {
			if (windowArray.get(i) == null) {
				windowArray.set(i, window);
				break exitLoop;
			}
		}
		
		if (notificationTimer == null) {
			notificationTimer = new Timer(30, new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					for (int i = 0; i<windowArray.length(); i++) {
						currentWindow = windowArray.get(i);
						if (currentWindow != null)
							currentWindow.updatePosition();
					}
					currentWindow = null;
				}
			});
			notificationTimer.start();
		}
	}
	
	private static class NotificationWindow extends JWindow {

		private static final long serialVersionUID = 1L;
		
		private int timer = 0;
		private int position = 0;

		NotificationWindow(String message, int icon, int position) {
			super();
			this.position = position;
			
			Dimension panelSize = new Dimension(350, 45);
			
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.setPreferredSize(panelSize);
			panel.setBorder(new EmptyBorder(0,1,0,1));
			
			JLabel label = new JLabel(new ImageIcon(Assets.loadImage("notifications/icon" + icon + ".png")));
			label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			panel.add(label);
			
			label = new JLabel(message);
			label.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_SMALL));
			label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			panel.add(label);
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (int)(screenSize.getWidth() - panelSize.getWidth());
			int y = (int)(screenSize.getHeight() - (panelSize.getHeight() + 10) * position);
			
			this.add(panel);
			this.pack();
			this.setLocation(x, y);
			this.setOpacity(0f);
			this.setVisible(true);
		}
		
		void updatePosition() {
			if (timer <= 15) {
				this.setOpacity(timer / 15f);
				this.setLocation(this.getLocationOnScreen().x, this.getLocationOnScreen().y - 3);
			} else if (timer <= 165) {
				
			} else if (timer <= 180) {
				this.setOpacity(-(timer - 180) / 15f);
				this.setLocation(this.getLocationOnScreen().x, this.getLocationOnScreen().y + 3);
			} else {
				isNotificationPositionFilled[position].set(false);
				this.dispose();
				for (int i = 0; i<windowArray.length(); i++)
					windowArray.compareAndSet(i, this, null);
				
				if (currentNotificationCount.decrementAndGet() == 0) {
					notificationTimer.stop();
					notificationTimer = null;
				}
			}
			timer++;
		}
	}
}
