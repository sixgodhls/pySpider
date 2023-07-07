package me.goldze.mvvmhabit.bus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

/* loaded from: classes.dex */
public class Messenger {
    private static Messenger defaultInstance;
    private HashMap<Type, List<WeakActionAndToken>> recipientsOfSubclassesAction;
    private HashMap<Type, List<WeakActionAndToken>> recipientsStrictAction;

    /* loaded from: classes.dex */
    public static class NotMsgType {
    }

    public static Messenger getDefault() {
        if (defaultInstance == null) {
            defaultInstance = new Messenger();
        }
        return defaultInstance;
    }

    public static void overrideDefault(Messenger newWeakMessenger) {
        defaultInstance = newWeakMessenger;
    }

    public static void reset() {
        defaultInstance = null;
    }

    public void register(Object recipient, BindingAction action) {
        register(recipient, (Object) null, false, action);
    }

    public void register(Object recipient, boolean receiveDerivedMessagesToo, BindingAction action) {
        register(recipient, (Object) null, receiveDerivedMessagesToo, action);
    }

    public void register(Object recipient, Object token, BindingAction action) {
        register(recipient, token, false, action);
    }

    public void register(Object recipient, Object token, boolean receiveDerivedMessagesToo, BindingAction action) {
        HashMap<Type, List<WeakActionAndToken>> recipients;
        List<WeakActionAndToken> list;
        if (receiveDerivedMessagesToo) {
            if (this.recipientsOfSubclassesAction == null) {
                this.recipientsOfSubclassesAction = new HashMap<>();
            }
            recipients = this.recipientsOfSubclassesAction;
        } else {
            if (this.recipientsStrictAction == null) {
                this.recipientsStrictAction = new HashMap<>();
            }
            recipients = this.recipientsStrictAction;
        }
        if (!recipients.containsKey(NotMsgType.class)) {
            list = new ArrayList<>();
            recipients.put(NotMsgType.class, list);
        } else {
            List<WeakActionAndToken> list2 = recipients.get(NotMsgType.class);
            list = list2;
        }
        WeakAction weakAction = new WeakAction(recipient, action);
        WeakActionAndToken item = new WeakActionAndToken(weakAction, token);
        list.add(item);
        cleanup();
    }

    public <T> void register(Object recipient, Class<T> tClass, BindingConsumer<T> action) {
        register(recipient, null, false, action, tClass);
    }

    public <T> void register(Object recipient, boolean receiveDerivedMessagesToo, Class<T> tClass, BindingConsumer<T> action) {
        register(recipient, null, receiveDerivedMessagesToo, action, tClass);
    }

    public <T> void register(Object recipient, Object token, Class<T> tClass, BindingConsumer<T> action) {
        register(recipient, token, false, action, tClass);
    }

    public <T> void register(Object recipient, Object token, boolean receiveDerivedMessagesToo, BindingConsumer<T> action, Class<T> tClass) {
        HashMap<Type, List<WeakActionAndToken>> recipients;
        List<WeakActionAndToken> list;
        if (receiveDerivedMessagesToo) {
            if (this.recipientsOfSubclassesAction == null) {
                this.recipientsOfSubclassesAction = new HashMap<>();
            }
            recipients = this.recipientsOfSubclassesAction;
        } else {
            if (this.recipientsStrictAction == null) {
                this.recipientsStrictAction = new HashMap<>();
            }
            recipients = this.recipientsStrictAction;
        }
        if (!recipients.containsKey(tClass)) {
            list = new ArrayList<>();
            recipients.put(tClass, list);
        } else {
            List<WeakActionAndToken> list2 = recipients.get(tClass);
            list = list2;
        }
        WeakAction weakAction = new WeakAction(recipient, action);
        WeakActionAndToken item = new WeakActionAndToken(weakAction, token);
        list.add(item);
        cleanup();
    }

    private void cleanup() {
        cleanupList(this.recipientsOfSubclassesAction);
        cleanupList(this.recipientsStrictAction);
    }

    public void sendNoMsg(Object token) {
        sendToTargetOrType(null, token);
    }

    public void sendNoMsgToTarget(Object target) {
        sendToTargetOrType(target.getClass(), null);
    }

    public void sendNoMsgToTargetWithToken(Object token, Object target) {
        sendToTargetOrType(target.getClass(), token);
    }

    public <T> void send(T message) {
        sendToTargetOrType(message, null, null);
    }

    public <T> void send(T message, Object token) {
        sendToTargetOrType(message, null, token);
    }

    public <T, R> void sendToTarget(T message, R target) {
        sendToTargetOrType(message, target.getClass(), null);
    }

    public void unregister(Object recipient) {
        unregisterFromLists(recipient, this.recipientsOfSubclassesAction);
        unregisterFromLists(recipient, this.recipientsStrictAction);
        cleanup();
    }

    public <T> void unregister(Object recipient, Object token) {
        unregisterFromLists(recipient, token, (BindingAction) null, this.recipientsStrictAction);
        unregisterFromLists(recipient, token, (BindingAction) null, this.recipientsOfSubclassesAction);
        cleanup();
    }

    private static <T> void sendToList(T message, Collection<WeakActionAndToken> list, Type messageTargetType, Object token) {
        if (list != null) {
            ArrayList<WeakActionAndToken> listClone = new ArrayList<>();
            listClone.addAll(list);
            Iterator<WeakActionAndToken> it = listClone.iterator();
            while (it.hasNext()) {
                WeakActionAndToken item = it.next();
                WeakAction executeAction = item.getAction();
                if (executeAction != null && item.getAction().isLive() && item.getAction().getTarget() != null && (messageTargetType == null || item.getAction().getTarget().getClass() == messageTargetType || classImplements(item.getAction().getTarget().getClass(), messageTargetType))) {
                    if ((item.getToken() == null && token == null) || (item.getToken() != null && item.getToken().equals(token))) {
                        executeAction.execute(message);
                    }
                }
            }
        }
    }

    private static void unregisterFromLists(Object recipient, HashMap<Type, List<WeakActionAndToken>> lists) {
        if (recipient == null || lists == null || lists.size() == 0) {
            return;
        }
        synchronized (lists) {
            for (Type messageType : lists.keySet()) {
                for (WeakActionAndToken item : lists.get(messageType)) {
                    WeakAction weakAction = item.getAction();
                    if (weakAction != null && recipient == weakAction.getTarget()) {
                        weakAction.markForDeletion();
                    }
                }
            }
        }
        cleanupList(lists);
    }

    private static <T> void unregisterFromLists(Object recipient, BindingConsumer<T> action, HashMap<Type, List<WeakActionAndToken>> lists, Class<T> tClass) {
        if (recipient == null || lists == null || lists.size() == 0 || !lists.containsKey(tClass)) {
            return;
        }
        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(tClass)) {
                WeakAction<T> weakActionCasted = item.getAction();
                if (weakActionCasted != null && recipient == weakActionCasted.getTarget() && (action == null || action == weakActionCasted.getBindingConsumer())) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }

    private static void unregisterFromLists(Object recipient, BindingAction action, HashMap<Type, List<WeakActionAndToken>> lists) {
        if (recipient == null || lists == null || lists.size() == 0 || !lists.containsKey(NotMsgType.class)) {
            return;
        }
        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(NotMsgType.class)) {
                WeakAction weakActionCasted = item.getAction();
                if (weakActionCasted != null && recipient == weakActionCasted.getTarget() && (action == null || action == weakActionCasted.getBindingAction())) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }

    private static <T> void unregisterFromLists(Object recipient, Object token, BindingConsumer<T> action, HashMap<Type, List<WeakActionAndToken>> lists, Class<T> tClass) {
        if (recipient == null || lists == null || lists.size() == 0 || !lists.containsKey(tClass)) {
            return;
        }
        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(tClass)) {
                WeakAction<T> weakActionCasted = item.getAction();
                if (weakActionCasted != null && recipient == weakActionCasted.getTarget() && ((action == null || action == weakActionCasted.getBindingConsumer()) && (token == null || token.equals(item.getToken())))) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }

    private static void unregisterFromLists(Object recipient, Object token, BindingAction action, HashMap<Type, List<WeakActionAndToken>> lists) {
        if (recipient == null || lists == null || lists.size() == 0 || !lists.containsKey(NotMsgType.class)) {
            return;
        }
        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(NotMsgType.class)) {
                WeakAction weakActionCasted = item.getAction();
                if (weakActionCasted != null && recipient == weakActionCasted.getTarget() && ((action == null || action == weakActionCasted.getBindingAction()) && (token == null || token.equals(item.getToken())))) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }

    private static boolean classImplements(Type instanceType, Type interfaceType) {
        if (interfaceType == null || instanceType == null) {
            return false;
        }
        Class[] interfaces = ((Class) instanceType).getInterfaces();
        for (Class currentInterface : interfaces) {
            if (currentInterface == interfaceType) {
                return true;
            }
        }
        return false;
    }

    private static void cleanupList(HashMap<Type, List<WeakActionAndToken>> lists) {
        if (lists == null) {
            return;
        }
        for (Object key : lists.entrySet()) {
            List<WeakActionAndToken> itemList = lists.get(key);
            if (itemList != null) {
                for (WeakActionAndToken item : itemList) {
                    if (item.getAction() == null || !item.getAction().isLive()) {
                        itemList.remove(item);
                    }
                }
                if (itemList.size() == 0) {
                    lists.remove(key);
                }
            }
        }
    }

    private void sendToTargetOrType(Type messageTargetType, Object token) {
        if (this.recipientsOfSubclassesAction != null) {
            List<Type> listClone = new ArrayList<>();
            listClone.addAll(this.recipientsOfSubclassesAction.keySet());
            for (Type type : listClone) {
                List<WeakActionAndToken> list = null;
                if (NotMsgType.class == type || ((Class) type).isAssignableFrom(NotMsgType.class) || classImplements(NotMsgType.class, type)) {
                    List<WeakActionAndToken> list2 = this.recipientsOfSubclassesAction.get(type);
                    list = list2;
                }
                sendToList(list, messageTargetType, token);
            }
        }
        HashMap<Type, List<WeakActionAndToken>> hashMap = this.recipientsStrictAction;
        if (hashMap != null && hashMap.containsKey(NotMsgType.class)) {
            List<WeakActionAndToken> list3 = this.recipientsStrictAction.get(NotMsgType.class);
            sendToList(list3, messageTargetType, token);
        }
        cleanup();
    }

    private static void sendToList(Collection<WeakActionAndToken> list, Type messageTargetType, Object token) {
        if (list != null) {
            ArrayList<WeakActionAndToken> listClone = new ArrayList<>();
            listClone.addAll(list);
            Iterator<WeakActionAndToken> it = listClone.iterator();
            while (it.hasNext()) {
                WeakActionAndToken item = it.next();
                WeakAction executeAction = item.getAction();
                if (executeAction != null && item.getAction().isLive() && item.getAction().getTarget() != null && (messageTargetType == null || item.getAction().getTarget().getClass() == messageTargetType || classImplements(item.getAction().getTarget().getClass(), messageTargetType))) {
                    if ((item.getToken() == null && token == null) || (item.getToken() != null && item.getToken().equals(token))) {
                        executeAction.execute();
                    }
                }
            }
        }
    }

    private <T> void sendToTargetOrType(T message, Type messageTargetType, Object token) {
        Class messageType = message.getClass();
        if (this.recipientsOfSubclassesAction != null) {
            List<Type> listClone = new ArrayList<>();
            listClone.addAll(this.recipientsOfSubclassesAction.keySet());
            for (Type type : listClone) {
                List<WeakActionAndToken> list = null;
                if (messageType == type || ((Class) type).isAssignableFrom(messageType) || classImplements(messageType, type)) {
                    List<WeakActionAndToken> list2 = this.recipientsOfSubclassesAction.get(type);
                    list = list2;
                }
                sendToList(message, list, messageTargetType, token);
            }
        }
        HashMap<Type, List<WeakActionAndToken>> hashMap = this.recipientsStrictAction;
        if (hashMap != null && hashMap.containsKey(messageType)) {
            List<WeakActionAndToken> list3 = this.recipientsStrictAction.get(messageType);
            sendToList(message, list3, messageTargetType, token);
        }
        cleanup();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WeakActionAndToken {
        private WeakAction action;
        private Object token;

        public WeakActionAndToken(WeakAction action, Object token) {
            this.action = action;
            this.token = token;
        }

        public WeakAction getAction() {
            return this.action;
        }

        public void setAction(WeakAction action) {
            this.action = action;
        }

        public Object getToken() {
            return this.token;
        }

        public void setToken(Object token) {
            this.token = token;
        }
    }
}
