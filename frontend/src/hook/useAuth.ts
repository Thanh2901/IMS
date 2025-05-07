import { useLoginMutation, useRegisterMutation, useLogoutMutation, authApi } from "../redux/service/auth";
import { useNavigate } from "react-router-dom";
import { LoginRequest, RegisterRequest } from "../type/models";
import useCustomToast from "./useCustomToast";
import { useDispatch } from "react-redux";
import { userApi } from "../redux/service/user";
import { infrastructureApi } from "../redux/service/infrastructure";
import { notificationApi } from "../redux/service/notification";
import { cameraApi } from "../redux/service/camera";
import { reportApi } from "../redux/service/report";

const useAuth = () => {
  const showToast = useCustomToast();
  const navigate = useNavigate();
  const [register] = useRegisterMutation();
  const [login] = useLoginMutation();
  const [logout] = useLogoutMutation();
  const dispatch = useDispatch();

  const isLoggedIn = () => {
    const token = localStorage.getItem("access_token");
    if (!token) {
      return false;
    }
    return true;
  };

  const registerFunc = async (data: RegisterRequest) => {
    try {
      await register({requestBody: data})
      .unwrap()
      .then((response) => {
        console.log(response);
        showToast("Account Registration", "Register successfully", "success");
        navigate("/login");
      });
    } catch (err: any) {
      const errorMessage = err.data?.message || "Internal server error";
      showToast("Error", errorMessage, "error");
    }
  };

  const loginFunc = async (data: LoginRequest) => {
    try {
      await login({requestBody: data})
      .unwrap()
      .then((response) => {
        console.log(response);
        showToast("Account Login", "Login successfully", "success");
        localStorage.setItem("access_token", response.token);

        // a hack to reset every apis manually to trigger
        // but there should be a better solution no ?
        dispatch(userApi.util.resetApiState());
        dispatch(infrastructureApi.util.resetApiState());
        dispatch(reportApi.util.resetApiState());
        dispatch(notificationApi.util.resetApiState());
        dispatch(cameraApi.util.resetApiState());

        navigate("/");
      })
    } catch(err: any) {
      const errorMessage = err.data?.message || "Internal server error";
      showToast("Error", errorMessage, "error");
    }
  };

  const logoutFunc = () => {
    logout();
    localStorage.removeItem("access_token");
    navigate("/login");
  };

  return { registerFunc, loginFunc, logoutFunc, isLoggedIn };
};

export default useAuth;