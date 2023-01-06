package com.aservice.controller;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aservice.dao.MessageDao;
import com.aservice.dao.UserDao;
import com.aservice.entity.Block;
import com.aservice.entity.Message;
import com.aservice.entity.Offer;
import com.aservice.entity.User;
import com.aservice.util.SharedUtil;
import com.aservice.util.UserUtil;
import com.aservice.util.modifiers.MessagesModifier;
import com.aservice.util.modifiers.Modifier;
import com.aservice.util.modifiers.UserContactsModifier;

@Controller
@RequestMapping("/message")
public class MessageController {

	@Autowired
	private UserDao userDao;
	@Autowired
	private MessageDao messageDao;
	
	@RequestMapping(value = "/create/{receiverId}", method = {RequestMethod.GET, RequestMethod.POST})
	public String createMessage(@PathVariable("receiverId") int receiverId,
								RedirectAttributes redirectAttributes) {
		
		User currentUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());	
		
		if(currentUser.getId()== receiverId) {
			redirectAttributes.addFlashAttribute("info", "messageFail");
			redirectAttributes.addFlashAttribute("givenName", UserUtil.getLoggedUserName());
			return "redirect:/main/";
		}
		
		// if current user has blocked target user
		if(messageDao.getBlock(currentUser.getId(), receiverId)!=null) {
			redirectAttributes.addFlashAttribute("info", "youHaveBlocked");
			redirectAttributes.addFlashAttribute("givenName", UserUtil.getLoggedUserName());
			return "redirect:/main/";
		}
		
		// if target user has blocked current user
		if(messageDao.getBlock(receiverId, currentUser.getId())!=null) {
			redirectAttributes.addFlashAttribute("info", "youAreBlocked");
			redirectAttributes.addFlashAttribute("givenName", UserUtil.getLoggedUserName());
			return "redirect:/main/";
		}
		
		Message message = new Message();
		message.setReceiverId(receiverId);
		
		Modifier listModifier = new MessagesModifier();
		((MessagesModifier) listModifier).setReceiverId(receiverId);
		
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addFlashAttribute("listModifier", listModifier);
		
		return "redirect:/message/create/list/show";
	}
	
	@RequestMapping(value = "/create/list/{task}", method = {RequestMethod.GET, RequestMethod.POST})
	public String createMessagePaged(@PathVariable("task") String task,
									 @ModelAttribute("message") Message message,
									 @ModelAttribute("listModifier") MessagesModifier listModifier,
									 Model model) {
		
		// if thymeleaf parses object with comma at the beginning
		if(SharedUtil.checkIfFilterValid(listModifier.getFilter()) && listModifier.getFilter().startsWith(",")) {
			listModifier.setFilter(listModifier.getFilter().substring(1));
		}
		
		// user button click
		switch (task) {
		case "show": {
			listModifier.setShowClicked();
			break;
		}
		case "left": {
			if(listModifier.getPreviousPage()<=0) return "redirect:/main/";
			listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
			listModifier.decrement();
			break;
		}
		case "right":{
			if(!listModifier.getIsNext()) return "redirect:/main/";
			listModifier.setStartingRow(listModifier.getStartingRow()+listModifier.getLimit());
			listModifier.increment();
			break;
		}
		default:
			return "redirect:/main/";
		}
		
		if(listModifier.getReceiverId()==0) return "redirect:/message/list/contacts";
		
		User currentUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		List<Message> dbMessages = null;

		dbMessages = messageDao.getMessagesWithUser(listModifier,currentUser.getId(),
		listModifier.getReceiverId());
		
		listModifier.setStartingRow(listModifier.getStartingRow()+listModifier.getLimit());
		if(messageDao.getMessagesWithUser(listModifier,currentUser.getId(),
				listModifier.getReceiverId())!=null) 
			listModifier.setIsNext(true);
		else
			listModifier.setIsNext(false);
		listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
		
		if(dbMessages==null) dbMessages = new ArrayList<>();
		Map<Message,Boolean> messageMap = new LinkedHashMap<>();
		
		dbMessages.forEach(messageFromList->{
			if(messageFromList.getUser().getId()==currentUser.getId())
				messageMap.put(messageFromList,true);
			else
				messageMap.put(messageFromList,false);
		});
		
		String receiverName = userDao.getUserById(listModifier.getReceiverId()).getUsername();
		
		model.addAttribute("message", message);
		model.addAttribute("messageMap", messageMap);
		model.addAttribute("listModifier", listModifier);
		model.addAttribute("receiverName", receiverName);
		
		return "message/message-form";
	}

	@PostMapping("/send")
	public String sendMessage(@ModelAttribute("message") Message message,
							  RedirectAttributes redirectAttributes) {
		
		User sender = userDao.getUserByUsername(UserUtil.getLoggedUserName());
	
		message.setMessageDate(new Timestamp(System.currentTimeMillis()));
		sender.addMessage(message);
		message.setUser(sender);
		messageDao.addMessage(message);
		
		redirectAttributes.addFlashAttribute("info", "messageSuccess");
		redirectAttributes.addFlashAttribute("givenName", UserUtil.getLoggedUserName());
		return "redirect:/main/";
	}
	
	@GetMapping("/list/contacts")
	public String listContacts(RedirectAttributes redirectAttributes) {
		
		Modifier listModifier = new UserContactsModifier();
		
		redirectAttributes.addFlashAttribute("listModifier", listModifier);
		
		return "redirect:/message/list/contacts/show";
	}
	
	@GetMapping("/list/contacts/{task}")
	public String viewAllContacts(@PathVariable("task") String task,
								  @ModelAttribute UserContactsModifier listModifier,
								  Model model) {
		
		// if thymeleaf parses object with comma at the beginning
		if(SharedUtil.checkIfFilterValid(listModifier.getFilter()) && listModifier.getFilter().startsWith(",")) {
			listModifier.setFilter(listModifier.getFilter().substring(1));
		}
		
		// user button click
		switch (task) {
		case "show": {
			listModifier.setShowClicked();
			break;
		}
		case "left": {
			if(listModifier.getPreviousPage()<=0) return "redirect:/main/";
			listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
			listModifier.decrement();
			break;
		}
		case "right":{
			if(!listModifier.getIsNext()) return "redirect:/main/";
			listModifier.setStartingRow(listModifier.getStartingRow()+listModifier.getLimit());
			listModifier.increment();
			break;
		}
		default:
			return "redirect:/main/";
		}
		
		User currentUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		List<User> dbContactsNoMess = null;
		dbContactsNoMess = messageDao.getContacts(listModifier,currentUser.getId());
		
		listModifier.setStartingRow(listModifier.getStartingRow()+listModifier.getLimit());
		if(messageDao.getContacts(listModifier,currentUser.getId())!=null) 
			listModifier.setIsNext(true);
		else
			listModifier.setIsNext(false);
		listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
		
		if(dbContactsNoMess==null) dbContactsNoMess = new ArrayList<>();
		
		int loggedUserId = userDao.getUserByUsername(UserUtil.getLoggedUserName()).getId();
		
		Map<User,Message> dbContacts = new LinkedHashMap<>();
		dbContactsNoMess.forEach(contact->{
			
			Comparator<Message> dateComparator = Comparator.comparing(Message::getMessageDate);
			
			Message mostRecentMessage = null;
			
			try {
				mostRecentMessage = 
						contact.getMessages()
						.stream()
						.filter(message -> message.getReceiverId()==loggedUserId)
						.max(dateComparator).get();
			}
			catch (Exception exception) {
			
			}
			
			dbContacts.put(contact, mostRecentMessage);
		});
		
		
		model.addAttribute("contacts", dbContacts);
		model.addAttribute("listModifier", listModifier);
		model.addAttribute("loggedUserId", loggedUserId);
		
		return "message/contact-list";
	}
	
	@GetMapping("/block/{userToBlockId}")
	public String blockUser(@PathVariable("userToBlockId") int userToBlockId) {
		
		User currentUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		
		if(userToBlockId==currentUser.getId())
			return "redirect:/user/viewprofile/picked/"+userToBlockId;
		
		Block block = new Block();
		block.setUser(currentUser);
		block.setBlockedUserId(userToBlockId);
		messageDao.addBlock(block);
		
		return "redirect:/user/viewprofile/picked/"+userToBlockId;
	}
	
	@GetMapping("/unblock/{userToUnblockId}")
	public String unblockUser(@PathVariable("userToUnblockId") int userToUnblockId) {
		
		User currentUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		
		if(userToUnblockId==currentUser.getId())
			return "redirect:/user/viewprofile/picked/"+userToUnblockId;

		Block blockToDelete = messageDao.getBlock(currentUser.getId(), userToUnblockId);
		messageDao.deleteBlock(blockToDelete);
		
		return "redirect:/user/viewprofile/picked/"+userToUnblockId;
	}
}
