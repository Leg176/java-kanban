public enum Status {
    NEW,
    IN_PROGRESS,
    DONE;

    public static Status toString(String text){
        switch(text){
            case "NEW" :
                return Status.NEW;
            case "IN_PROGRESS" :
                return Status.IN_PROGRESS;
            case "DONE" :
                return Status.DONE;
        }
        return null;
    }

}

